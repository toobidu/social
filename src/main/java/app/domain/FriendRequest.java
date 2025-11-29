package app.domain;

import app.domain.enumeration.FriendRequestStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Lời mời kết bạn (Đã nhận).
 */
@Table("friend_requests_received")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "receiver_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID receiverId; // ID người nhận

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant createdAt; // Thời gian tạo

    @PrimaryKeyColumn(name = "sender_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID senderId; // ID người gửi

    @Column("sender_name")
    private String senderName; // Tên người gửi

    @Column("sender_avatar")
    private String senderAvatar; // Avatar người gửi

    private FriendRequestStatus status; // Trạng thái (PENDING, REJECTED)

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}
