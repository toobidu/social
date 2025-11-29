package app.criteria.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho PostLike.
 */
@Data
public class PostLikeSearchCriteria {

    private UUID postId;

    private UUID userId;

    private String userName;

    private Instant likedAt;

    private List<UUID> postIds;

    private List<UUID> userIds;

    private List<String> userNames;
}
