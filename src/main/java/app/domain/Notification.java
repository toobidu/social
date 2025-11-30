package app.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * A Notification.
 */
@Table("notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId; // The user receiving the notification

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id; // TimeUUID

    @Column("type")
    private String type; // LIKE, COMMENT, FRIEND_REQ, ACCEPT

    @Column("actor_id")
    private UUID actorId; // Who triggered the notification

    @Column("actor_name")
    private String actorName;

    @Column("actor_avatar")
    private String actorAvatar;

    @Column("target_id")
    private UUID targetId; // PostId, CommentId, etc.

    @Column("message")
    private String message;

    @Column("read")
    private boolean read;

    @Column("created_date")
    private Instant createdDate;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorAvatar() {
        return actorAvatar;
    }

    public void setActorAvatar(String actorAvatar) {
        this.actorAvatar = actorAvatar;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return (
            "Notification{" +
            "userId=" +
            userId +
            ", id=" +
            id +
            ", type='" +
            type +
            '\'' +
            ", actorId=" +
            actorId +
            ", actorName='" +
            actorName +
            '\'' +
            ", message='" +
            message +
            '\'' +
            ", read=" +
            read +
            ", createdDate=" +
            createdDate +
            '}'
        );
    }
}
