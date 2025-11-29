package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho UserConversation.
 */
@Data
public class UserConversationSearchCriteria {

    private UUID userId;

    private UUID conversationId;

    private String conversationName;

    private List<UUID> userIds;

    private List<UUID> conversationIds;

    private List<String> conversationNames;
}
