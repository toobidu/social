package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho Conversation.
 */
@Data
public class ConversationSearchCriteria {

    private UUID conversationId;

    private String name;

    private Boolean isGroup;

    private UUID participantId;

    private List<UUID> conversationIds;

    private List<String> names;

    private List<UUID> participantIds;
}
