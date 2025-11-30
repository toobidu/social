package app.domain;

import app.domain.enumeration.MediaType;
import app.domain.enumeration.PostStatus;
import app.domain.enumeration.PrivacyLevel;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Bài viết.
 */
@Table("posts")
@Document(indexName = "post")
public class Post extends AbstractAuditingEntity<Post> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey("post_id")
    @CassandraType(type = CassandraType.Name.TIMEUUID)
    private UUID postId; // ID bài viết

    @Column("user_id")
    private UUID userId; // ID người đăng

    private String content; // Nội dung bài viết

    @Column("media_urls")
    private List<String> mediaUrls; // Danh sách URL media

    @Column("media_type")
    private MediaType mediaType; // Loại media (IMAGE, VIDEO, ALBUM)

    @Column("privacy_level")
    private PrivacyLevel privacyLevel; // Mức độ riêng tư (PUBLIC, FRIENDS, PRIVATE)

    private PostStatus status; // Trạng thái (ACTIVE, DELETED, ARCHIVED)

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
