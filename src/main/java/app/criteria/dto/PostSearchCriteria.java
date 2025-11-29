package app.criteria.dto;

import app.domain.enumeration.MediaType;
import app.domain.enumeration.PostStatus;
import app.domain.enumeration.PrivacyLevel;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho Post.
 */
@Data
public class PostSearchCriteria {

    private UUID postId;

    private UUID userId;

    private String content;

    private MediaType mediaType;

    private PrivacyLevel privacyLevel;

    private PostStatus status;

    private List<UUID> postIds;

    private List<UUID> userIds;

    private List<String> contents;

    private List<MediaType> mediaTypes;

    private List<PrivacyLevel> privacyLevels;

    private List<PostStatus> statuses;
}
