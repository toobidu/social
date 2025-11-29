package app.domain;

import app.domain.enumeration.MediaType;
import app.domain.enumeration.PostStatus;
import app.domain.enumeration.PrivacyLevel;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * A Post.
 */
@Table("posts")
public class Post extends AbstractAuditingEntity<Post> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey("post_id")
    private UUID postId;

    @Column("user_id")
    private UUID userId;

    private String content;

    @Column("media_urls")
    private List<String> mediaUrls;

    @Column("media_type")
    private MediaType mediaType; // IMAGE, VIDEO, ALBUM

    @Column("privacy_level")
    private PrivacyLevel privacyLevel; // PUBLIC, FRIENDS, PRIVATE

    private PostStatus status; // ACTIVE, DELETED, ARCHIVED

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }
}
