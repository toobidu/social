package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho Comment.
 */
@Data
public class CommentSearchCriteria {

    private UUID postId;

    private UUID userId;

    private String content;

    private List<UUID> userIds;

    private List<UUID> postIds;

    private List<String> contents;
}
