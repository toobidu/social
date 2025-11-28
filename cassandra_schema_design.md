# Cassandra Schema Design for Social Network

This document outlines the database schema design for a scalable social network application using Apache Cassandra.

## 1. Keyspace Configuration

For a production environment with millions of users, we use `NetworkTopologyStrategy` for multi-datacenter support.

```sql
CREATE KEYSPACE IF NOT EXISTS social_network
WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'datacenter1': 3, -- 3 replicas in main DC
    'datacenter2': 3  -- 3 replicas in backup/analytics DC
};
```

## 2. User Management

Designed to support fast lookups by ID, Email, and Username.

### Tables

```sql
-- Main User Table
CREATE TABLE users (
    user_id UUID,
    username text,
    email text,
    password_hash text,
    full_name text,
    avatar_url text,
    bio text,
    is_active boolean, -- For soft delete
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (user_id)
);

-- Lookup table: Login by Email
CREATE TABLE users_by_email (
    email text,
    user_id UUID,
    password_hash text, -- Optional: to validate pass without 2nd query
    PRIMARY KEY (email)
);

-- Lookup table: Search/Login by Username
CREATE TABLE users_by_username (
    username text,
    user_id UUID,
    PRIMARY KEY (username)
);
```

**Notes:**

- **Write Path**: When creating a user, write to all 3 tables (Batch or Saga).
- **GDPR/Delete**: Set `is_active = false`. For hard delete, issue DELETE on all tables with TTL.

## 3. Social Graph (Friends)

### Tables

```sql
-- List of friends for a user
CREATE TABLE friends (
    user_id UUID,
    friend_id UUID,
    friend_name text, -- Denormalized for UI display
    friend_avatar text, -- Denormalized
    since timestamp,
    PRIMARY KEY (user_id, friend_id)
);

-- Pending Friend Requests (Received)
CREATE TABLE friend_requests_received (
    receiver_id UUID,
    sender_id UUID,
    sender_name text,
    sender_avatar text,
    created_at timestamp,
    status text, -- PENDING, REJECTED
    PRIMARY KEY (receiver_id, created_at, sender_id)
) WITH CLUSTERING ORDER BY (created_at DESC);

-- Sent Friend Requests (Optional, for UI "Cancel Request")
CREATE TABLE friend_requests_sent (
    sender_id UUID,
    receiver_id UUID,
    created_at timestamp,
    PRIMARY KEY (sender_id, receiver_id)
);

-- Blocked Users
CREATE TABLE user_blocks (
    user_id UUID,
    blocked_user_id UUID,
    created_at timestamp,
    PRIMARY KEY (user_id, blocked_user_id)
);
```

## 4. Content (Posts)

### Tables

```sql
-- Main Post Storage
CREATE TABLE posts (
    post_id timeuuid, -- Contains timestamp
    user_id UUID,
    content text,
    media_urls list<text>, -- Images/Videos
    media_type text, -- IMAGE, VIDEO, ALBUM
    privacy_level text, -- PUBLIC, FRIENDS, PRIVATE
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (post_id)
);

-- User's Wall / Profile Timeline
-- Query: Get all posts by User X
CREATE TABLE user_posts (
    user_id UUID,
    post_id timeuuid,
    content text, -- Denormalized preview
    media_urls list<text>,
    created_at timestamp,
    PRIMARY KEY (user_id, post_id)
) WITH CLUSTERING ORDER BY (post_id DESC);
```

## 5. News Feed (The "Wall")

We use a **Fan-out on Write** (Push) model for standard users. When User A posts, we push the Post ID to all friends' feed tables.

### Tables

```sql
-- The News Feed
CREATE TABLE news_feed (
    user_id UUID,
    post_id timeuuid,
    author_id UUID,
    author_name text, -- Denormalized
    author_avatar text, -- Denormalized
    snippet text, -- First 100 chars
    PRIMARY KEY (user_id, post_id)
) WITH CLUSTERING ORDER BY (post_id DESC);
```

**Strategy:**

1.  **Write**: User A creates a post.
2.  **Async Job**: Find all friends of User A (from `friends` table).
3.  **Batch Insert**: Insert entry into `news_feed` for every friend.
4.  **Read**: `SELECT * FROM news_feed WHERE user_id = ? LIMIT 20` (Extremely fast).

## 6. Interactions (Likes & Comments)

### Tables

```sql
-- Who liked a post?
CREATE TABLE post_likes (
    post_id timeuuid,
    user_id UUID,
    user_name text,
    liked_at timestamp,
    PRIMARY KEY (post_id, user_id)
);

-- Counter table for fast stats (Optional but recommended for high scale)
CREATE TABLE post_counters (
    post_id timeuuid,
    likes_count counter,
    comments_count counter,
    shares_count counter,
    PRIMARY KEY (post_id)
);

-- Comments on a post
CREATE TABLE comments_by_post (
    post_id timeuuid,
    comment_id timeuuid,
    user_id UUID,
    user_name text,
    content text,
    created_at timestamp,
    PRIMARY KEY (post_id, comment_id)
) WITH CLUSTERING ORDER BY (comment_id ASC);
```

## 7. Messaging (Chat)

Using the "Inbox" pattern.

### Tables

```sql
-- Metadata for a conversation (1-on-1 or Group)
CREATE TABLE conversations (
    conversation_id UUID,
    name text, -- Group name
    is_group boolean,
    created_at timestamp,
    participant_ids set<UUID>,
    PRIMARY KEY (conversation_id)
);

-- User's Inbox (List of conversations)
CREATE TABLE user_conversations (
    user_id UUID,
    last_message_at timeuuid,
    conversation_id UUID,
    conversation_name text,
    unread_count int, -- Application level logic to maintain this, or separate counter
    PRIMARY KEY (user_id, last_message_at)
) WITH CLUSTERING ORDER BY (last_message_at DESC);

-- Messages in a conversation
-- Bucket pattern used to prevent partitions from getting too large (e.g., active group chats)
CREATE TABLE messages (
    conversation_id UUID,
    bucket int, -- e.g., Year-Month (202311) or fixed size bucket
    message_id timeuuid,
    sender_id UUID,
    content text,
    media_url text,
    created_at timestamp,
    PRIMARY KEY ((conversation_id, bucket), message_id)
) WITH CLUSTERING ORDER BY (message_id ASC);
```

## 8. Search (Limitations & Solution)

Cassandra is **not** a search engine. `ALLOW FILTERING` is bad for performance.

**Solution:**

1.  **Users**: Use `users_by_username` for exact match. For prefix search (e.g., "Dav"), use a separate Search Engine (Elasticsearch) or a dedicated Solr core.
2.  **Posts**: Full-text search must be offloaded to Elasticsearch.

_If you strictly must use Cassandra for simple user search:_

```sql
-- Search by name prefix (e.g., "joh" -> "john", "johnny")
-- Note: This has limitations (hot partitions).
CREATE TABLE users_by_name_prefix (
    prefix text,
    full_name text,
    user_id UUID,
    PRIMARY KEY (prefix, full_name, user_id)
);
```

## 9. Optimization & Best Practices

1.  **Denormalization**: We duplicate data (username, avatar) into `news_feed`, `comments`, etc., to avoid "Join" queries (which Cassandra doesn't support).
2.  **TimeUUID**: Used for IDs to ensure uniqueness and chronological sorting without a separate timestamp column in the key.
3.  **Bucketing**: Used in `messages` to keep partition sizes manageable (< 100MB).
4.  **Batching**: Use `LOGGED BATCH` only for keeping tables in sync (e.g., `users` and `users_by_email`). Do NOT use for bulk loading.
5.  **Tombstones**: For "Unfriend" or "Unlike", we delete rows. Run compaction regularly to clean up tombstones.
