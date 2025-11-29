package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho NewsFeed.
 */
@Data
public class NewsFeedSearchCriteria {

    private UUID userId;

    private UUID postId;

    private UUID authorId;

    private String authorName;

    private List<UUID> userIds;

    private List<UUID> postIds;

    private List<UUID> authorIds;

    private List<String> authorNames;
}
