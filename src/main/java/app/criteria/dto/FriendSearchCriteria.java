package app.criteria.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho Friend.
 */
@Data
public class FriendSearchCriteria {

    private UUID userId;

    private UUID friendId;

    private String friendName;

    private Instant since;

    private List<UUID> userIds;

    private List<UUID> friendIds;

    private List<String> friendNames;
}
