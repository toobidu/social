package app.domain;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Blocked Users.
 */
@Table("user_blocks")
public class UserBlock extends AbstractAuditingEntity<UserBlock> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "blocked_user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID blockedUserId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBlockedUserId() {
        return blockedUserId;
    }

    public void setBlockedUserId(UUID blockedUserId) {
        this.blockedUserId = blockedUserId;
    }
}
