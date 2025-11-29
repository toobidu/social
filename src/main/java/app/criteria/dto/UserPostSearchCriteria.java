package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho UserPost.
 */
@Data
public class UserPostSearchCriteria {

    private UUID userId;

    private UUID postId;

    private String content;

    private List<UUID> userIds;

    private List<UUID> postIds;

    private List<String> contents;
}
