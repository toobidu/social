package app.web.rest;

import app.domain.Friend;
import app.domain.FriendRequest;
import app.service.interfaces.FriendService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing Friends.
 */
@RestController
@RequestMapping("/api/friends")
public class FriendResource {

    private final Logger log = LoggerFactory.getLogger(FriendResource.class);

    private final FriendService friendService;

    public FriendResource(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * {@code POST  /request} : Send a friend request.
     *
     * @param senderId the sender id.
     * @param receiverId the receiver id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/request")
    public Mono<ResponseEntity<Void>> sendFriendRequest(@RequestParam UUID senderId, @RequestParam UUID receiverId) {
        log.debug("REST request to send FriendRequest from {} to {}", senderId, receiverId);
        return friendService.sendFriendRequest(senderId, receiverId).then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code POST  /accept} : Accept a friend request.
     *
     * @param receiverId the receiver id.
     * @param senderId the sender id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/accept")
    public Mono<ResponseEntity<Void>> acceptFriendRequest(@RequestParam UUID receiverId, @RequestParam UUID senderId) {
        log.debug("REST request to accept FriendRequest from {} by {}", senderId, receiverId);
        return friendService.acceptFriendRequest(receiverId, senderId).then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code POST  /reject} : Reject a friend request.
     *
     * @param receiverId the receiver id.
     * @param senderId the sender id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/reject")
    public Mono<ResponseEntity<Void>> rejectFriendRequest(@RequestParam UUID receiverId, @RequestParam UUID senderId) {
        log.debug("REST request to reject FriendRequest from {} by {}", senderId, receiverId);
        return friendService.rejectFriendRequest(receiverId, senderId).then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code GET  /requests} : Get pending friend requests.
     *
     * @param userId the user id.
     * @return the {@link Flux} of friend requests.
     */
    @GetMapping("/requests")
    public Flux<FriendRequest> getFriendRequests(@RequestParam UUID userId) {
        log.debug("REST request to get FriendRequests for {}", userId);
        return friendService.getFriendRequests(userId);
    }

    /**
     * {@code GET  /} : Get friends list.
     *
     * @param userId the user id.
     * @return the {@link Flux} of friends.
     */
    @GetMapping("")
    public Flux<Friend> getFriends(@RequestParam UUID userId) {
        log.debug("REST request to get Friends for {}", userId);
        return friendService.getFriends(userId);
    }

    /**
     * {@code DELETE  /} : Unfriend.
     *
     * @param userId the user id.
     * @param friendId the friend id.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("")
    public Mono<ResponseEntity<Void>> unfriend(@RequestParam UUID userId, @RequestParam UUID friendId) {
        log.debug("REST request to unfriend {} and {}", userId, friendId);
        return friendService.unfriend(userId, friendId).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
