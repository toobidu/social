package app.criteria.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho Message.
 */
@Data
public class MessageSearchCriteria {

    private UUID messageId;

    private UUID conversationId;

    private UUID senderId;

    private String content;

    private List<UUID> messageIds;

    private List<UUID> conversationIds;

    private List<UUID> senderIds;

    private List<String> contents;
}
