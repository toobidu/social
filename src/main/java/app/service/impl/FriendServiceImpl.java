package app.service.impl;

import app.domain.Friend;
import app.domain.FriendRequest;
import app.domain.Notification;
import app.domain.User;
import app.repository.FriendRepository;
import app.repository.FriendRequestRepository;
import app.repository.UserRepository;
import app.service.interfaces.FriendService;
import app.service.interfaces.NotificationService;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Friend} and {@link FriendRequest}.
 */
@Service
public class FriendServiceImpl implements FriendService {

    private static final Logger LOG = LoggerFactory.getLogger(FriendServiceImpl.class);

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FriendServiceImpl(
        FriendRepository friendRepository,
        FriendRequestRepository friendRequestRepository,
        UserRepository userRepository,
        NotificationService notificationService
    ) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Mono<Void> sendFriendRequest(UUID senderId, UUID receiverId) {
        LOG.debug("Request to send Friend Request from {} to {}", senderId, receiverId);
        if (senderId.equals(receiverId)) {
            return Mono.error(new IllegalArgumentException("Cannot send friend request to yourself"));
        }

        return userRepository
            .findById(senderId.toString())
            .flatMap(sender -> {
                FriendRequest request = new FriendRequest();
                request.setReceiverId(receiverId);
                request.setSenderId(senderId);
                request.setSenderName(sender.getLastName() + " " + sender.getFirstName());
                request.setSenderAvatar(sender.getAvatarUrl());
                request.setCreatedAt(Instant.now());

                return friendRequestRepository
                    .save(request)
                    .doOnSuccess(savedRequest -> {
                        // Create Notification
                        Notification notification = new Notification();
                        notification.setUserId(receiverId);
                        notification.setType("FRIEND_REQ");
                        notification.setActorId(senderId);
                        notification.setActorName(sender.getLastName() + " " + sender.getFirstName());
                        notification.setActorAvatar(sender.getAvatarUrl());
                        notification.setTargetId(senderId); // Target is the sender (to view profile)
                        notification.setMessage("sent you a friend request.");
                        notificationService.createNotification(notification);
                    });
            })
            .then();
    }

    @Override
    public Mono<Void> acceptFriendRequest(UUID senderId, UUID receiverId) {
        LOG.debug("Request to accept Friend Request from {} to {}", senderId, receiverId);

        return friendRequestRepository
            .findByReceiverIdAndSenderId(receiverId, senderId)
            .flatMap(request -> {
                return Mono.zip(userRepository.findById(senderId.toString()), userRepository.findById(receiverId.toString())).flatMap(
                    tuple -> {
                        User sender = tuple.getT1();
                        User receiver = tuple.getT2();

                        Friend friendForReceiver = new Friend();
                        friendForReceiver.setUserId(receiverId);
                        friendForReceiver.setFriendId(senderId);
                        friendForReceiver.setFriendName(sender.getLastName() + " " + sender.getFirstName());
                        friendForReceiver.setFriendAvatar(sender.getAvatarUrl());
                        friendForReceiver.setSince(Instant.now());

                        Friend friendForSender = new Friend();
                        friendForSender.setUserId(senderId);
                        friendForSender.setFriendId(receiverId);
                        friendForSender.setFriendName(receiver.getLastName() + " " + receiver.getFirstName());
                        friendForSender.setFriendAvatar(receiver.getAvatarUrl());
                        friendForSender.setSince(Instant.now());

                        return friendRepository
                            .save(friendForReceiver)
                            .then(friendRepository.save(friendForSender))
                            .then(friendRequestRepository.delete(request))
                            .doOnSuccess(v -> {
                                // Create Notification for Sender (that Receiver accepted)
                                Notification notification = new Notification();
                                notification.setUserId(senderId);
                                notification.setType("ACCEPT");
                                notification.setActorId(receiverId);
                                notification.setActorName(receiver.getLastName() + " " + receiver.getFirstName());
                                notification.setActorAvatar(receiver.getAvatarUrl());
                                notification.setTargetId(receiverId);
                                notification.setMessage("accepted your friend request.");
                                notificationService.createNotification(notification);
                            });
                    }
                );
            })
            .then();
    }

    @Override
    public Mono<Void> rejectFriendRequest(UUID senderId, UUID receiverId) {
        LOG.debug("Request to reject Friend Request from {} to {}", senderId, receiverId);
        return friendRequestRepository.findByReceiverIdAndSenderId(receiverId, senderId).flatMap(friendRequestRepository::delete);
    }

    @Override
    public Flux<FriendRequest> getFriendRequests(UUID userId) {
        LOG.debug("Request to get Friend Requests for User : {}", userId);
        return friendRequestRepository.findByReceiverId(userId);
    }

    @Override
    public Flux<Friend> getFriends(UUID userId) {
        LOG.debug("Request to get Friends for User : {}", userId);
        return friendRepository.findByUserId(userId);
    }

    @Override
    public Mono<Void> unfriend(UUID userId, UUID friendId) {
        LOG.debug("Request to unfriend : {} and {}", userId, friendId);

        Friend f1 = new Friend();
        f1.setUserId(userId);
        f1.setFriendId(friendId);

        Friend f2 = new Friend();
        f2.setUserId(friendId);
        f2.setFriendId(userId);

        return friendRepository.delete(f1).then(friendRepository.delete(f2));
    }
}
