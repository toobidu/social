package app.service.impl;

import app.criteria.dto.PostSearchCriteria;
import app.domain.NewsFeed;
import app.domain.Post;
import app.domain.User;
import app.domain.UserPost;
import app.repository.FriendRepository;
import app.repository.NewsFeedRepository;
import app.repository.PostRepository;
import app.repository.UserPostRepository;
import app.repository.UserRepository;
import app.repository.search.PostSearchRepository;
import app.service.interfaces.PostService;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
public class PostServiceImpl implements PostService {

    private static final Logger LOG = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;
    private final ReactiveElasticsearchOperations elasticsearchOperations;
    private final NewsFeedRepository newsFeedRepository;
    private final UserPostRepository userPostRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(
        PostRepository postRepository,
        PostSearchRepository postSearchRepository,
        ReactiveElasticsearchOperations elasticsearchOperations,
        NewsFeedRepository newsFeedRepository,
        UserPostRepository userPostRepository,
        FriendRepository friendRepository,
        UserRepository userRepository
    ) {
        this.postRepository = postRepository;
        this.postSearchRepository = postSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.newsFeedRepository = newsFeedRepository;
        this.userPostRepository = userPostRepository;
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Post> createPost(Post post) {
        LOG.debug("Request to create Post : {}", post);
        if (post.getPostId() == null) {
            post.setPostId(Uuids.timeBased());
        }
        if (post.getCreatedDate() == null) {
            post.setCreatedDate(Instant.now());
        }

        // Fetch author info first to populate NewsFeed
        return userRepository
            .findById(post.getUserId().toString())
            .flatMap(author -> {
                return postRepository
                    .save(post)
                    .flatMap(savedPost -> {
                        // 1. Save to UserPost (Timeline) - Synchronous (User wants to see their own post)
                        UserPost userPost = new UserPost();
                        userPost.setUserId(savedPost.getUserId());
                        userPost.setPostId(savedPost.getPostId());
                        userPost.setContent(savedPost.getContent());
                        userPost.setMediaUrls(savedPost.getMediaUrls());
                        userPost.setCreatedDate(savedPost.getCreatedDate());

                        return userPostRepository.save(userPost).thenReturn(savedPost);
                    })
                    .doOnSuccess(savedPost -> {
                        // 2. Fan-out to Friends' NewsFeed (Async / Fire-and-Forget)
                        friendRepository
                            .findByUserId(savedPost.getUserId())
                            .flatMap(
                                friend -> {
                                    NewsFeed newsFeed = new NewsFeed();
                                    newsFeed.setUserId(friend.getFriendId()); // Feed owner is the friend
                                    newsFeed.setPostId(savedPost.getPostId());
                                    newsFeed.setAuthorId(savedPost.getUserId());
                                    newsFeed.setAuthorName(author.getLastName() + " " + author.getFirstName());
                                    newsFeed.setAuthorAvatar(author.getAvatarUrl());
                                    newsFeed.setSnippet(
                                        savedPost.getContent() != null && savedPost.getContent().length() > 100
                                            ? savedPost.getContent().substring(0, 100)
                                            : savedPost.getContent()
                                    );
                                    return newsFeedRepository.save(newsFeed);
                                },
                                20
                            ) // Concurrency limit
                            .subscribe(null, e -> LOG.error("Error during fan-out for post {}", savedPost.getPostId(), e));
                    })
                    .doOnSuccess(savedPost -> {
                        // 3. Index in Elasticsearch (Async / Fire-and-Forget)
                        postSearchRepository
                            .save(savedPost)
                            .subscribe(null, e -> LOG.error("Error indexing post {}", savedPost.getPostId(), e));
                    });
            });
    }

    @Override
    public Mono<Post> updatePost(Post post) {
        LOG.debug("Request to update Post : {}", post);
        return postRepository
            .save(post)
            .doOnSuccess(savedPost -> {
                // Async Indexing
                postSearchRepository.save(savedPost).subscribe(null, e -> LOG.error("Error indexing post {}", savedPost.getPostId(), e));
            });
    }

    @Override
    public Flux<Post> findAll() {
        LOG.debug("Request to get all Posts");
        return postRepository.findAll();
    }

    @Override
    public Mono<Post> findOne(UUID id) {
        LOG.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Post : {}", id);
        return postRepository
            .deleteById(id)
            .doOnSuccess(v -> {
                // Async Delete from Index
                postSearchRepository.deleteById(id).subscribe(null, e -> LOG.error("Error deleting post index {}", id, e));
            });
    }

    @Override
    public Flux<Post> search(PostSearchCriteria criteria, Pageable pageable) {
        Criteria searchCriteria = new Criteria();

        if (criteria.getContent() != null) {
            searchCriteria = searchCriteria.and(new Criteria("content").contains(criteria.getContent()));
        }

        if (criteria.getMediaType() != null) {
            searchCriteria = searchCriteria.and(new Criteria("mediaType").is(criteria.getMediaType()));
        }

        if (criteria.getStatus() != null) {
            searchCriteria = searchCriteria.and(new Criteria("status").is(criteria.getStatus()));
        }

        if (criteria.getPrivacyLevel() != null) {
            searchCriteria = searchCriteria.and(new Criteria("privacyLevel").is(criteria.getPrivacyLevel()));
        }

        if (criteria.getUserId() != null) {
            searchCriteria = searchCriteria.and(new Criteria("userId").is(criteria.getUserId()));
        }

        CriteriaQuery query = new CriteriaQuery(searchCriteria).setPageable(pageable);

        return elasticsearchOperations.search(query, Post.class).map(SearchHit::getContent);
    }

    @Override
    public Flux<NewsFeed> getNewsFeed(UUID userId, Pageable pageable) {
        LOG.debug("Request to get NewsFeed for User : {}", userId);
        return newsFeedRepository.findByUserId(userId, pageable);
    }

    @Override
    public Flux<UserPost> getUserTimeline(UUID userId, Pageable pageable) {
        LOG.debug("Request to get Timeline for User : {}", userId);
        return userPostRepository.findByUserId(userId, pageable);
    }
}
