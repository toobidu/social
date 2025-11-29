package app.domain;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Tin nhắn trong một cuộc trò chuyện.
 */
@Table("messages")
public class Message extends AbstractAuditingEntity<Message> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "conversation_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID conversationId; // ID cuộc trò chuyện

    @PrimaryKeyColumn(name = "bucket", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private Integer bucket; // Bucket để phân trang

    @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private UUID messageId; // ID tin nhắn (TimeUUID)

    @Column("sender_id")
    private UUID senderId; // ID người gửi

    private String content; // Nội dung tin nhắn

    @Column("media_url")
    private String mediaUrl; // URL media (nếu có)

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getBucket() {
        return bucket;
    }

    public void setBucket(Integer bucket) {
        this.bucket = bucket;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
