package app.criteria.dto;

import app.domain.enumeration.FriendRequestStatus;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho FriendRequest.
 */
@Data
public class FriendRequestSearchCriteria {

    private UUID receiverId;

    private UUID senderId;

    private String senderName;

    private FriendRequestStatus status;

    private List<UUID> receiverIds;

    private List<UUID> senderIds;

    private List<String> senderNames;

    private List<FriendRequestStatus> statuses;
}
