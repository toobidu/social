package app.service.interfaces;

import app.domain.Friend;
import app.domain.FriendRequest;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing Friends and Friend Requests.
 */
public interface FriendService {
    /**
     * Send a friend request.
     *
     * @param senderId the sender's id.
     * @param receiverId the receiver's id.
     * @return completion signal.
     */
    Mono<Void> sendFriendRequest(UUID senderId, UUID receiverId);

    /**
     * Accept a friend request.
     *
     * @param receiverId the receiver's id (who accepts).
     * @param senderId the sender's id (who sent).
     * @return completion signal.
     */
    Mono<Void> acceptFriendRequest(UUID receiverId, UUID senderId);

    /**
     * Reject a friend request.
     *
     * @param receiverId the receiver's id.
     * @param senderId the sender's id.
     * @return completion signal.
     */
    Mono<Void> rejectFriendRequest(UUID receiverId, UUID senderId);

    /**
     * Get pending friend requests for a user.
     *
     * @param userId the user id.
     * @return list of friend requests.
     */
    Flux<FriendRequest> getFriendRequests(UUID userId);

    /**
     * Get friends list for a user.
     *
     * @param userId the user id.
     * @return list of friends.
     */
    Flux<Friend> getFriends(UUID userId);

    /**
     * Unfriend a user.
     *
     * @param userId the user id.
     * @param friendId the friend's id.
     * @return completion signal.
     */
    Mono<Void> unfriend(UUID userId, UUID friendId);
}
