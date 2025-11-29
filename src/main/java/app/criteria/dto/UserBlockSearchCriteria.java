package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho UserBlock.
 */
@Data
public class UserBlockSearchCriteria {

    private UUID userId;

    private UUID blockedUserId;

    private List<UUID> userIds;

    private List<UUID> blockedUserIds;
}
