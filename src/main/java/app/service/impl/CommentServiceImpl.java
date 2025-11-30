package app.service.impl;

import app.domain.Comment;
import app.domain.Notification;
import app.repository.CommentRepository;
import app.repository.PostRepository;
import app.repository.UserRepository;
import app.service.interfaces.CommentService;
import app.service.interfaces.NotificationService;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing Comments.
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public CommentServiceImpl(
        CommentRepository commentRepository,
        UserRepository userRepository,
        PostRepository postRepository,
        NotificationService notificationService
    ) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Mono<Comment> addComment(Comment comment) {
        LOG.debug("Request to add Comment : {}", comment);
        if (comment.getCommentId() == null) {
            comment.setCommentId(Uuids.timeBased());
        }
        if (comment.getCreatedDate() == null) {
            comment.setCreatedDate(Instant.now());
        }

        return userRepository
            .findById(comment.getUserId().toString())
            .flatMap(user -> {
                comment.setUserName(user.getLastName() + " " + user.getFirstName());
                comment.setUserAvatar(user.getAvatarUrl());
                return commentRepository
                    .save(comment)
                    .doOnSuccess(savedComment -> {
                        // Fetch Post to get Author ID
                        postRepository
                            .findById(savedComment.getPostId())
                            .subscribe(post -> {
                                if (!post.getUserId().equals(savedComment.getUserId())) {
                                    // Create Notification if commenter is not post author
                                    Notification notification = new Notification();
                                    notification.setUserId(post.getUserId()); // Post author receives notification
                                    notification.setType("COMMENT");
                                    notification.setActorId(savedComment.getUserId());
                                    notification.setActorName(savedComment.getUserName());
                                    notification.setActorAvatar(savedComment.getUserAvatar());
                                    notification.setTargetId(savedComment.getPostId());
                                    notification.setMessage("commented on your post.");
                                    notificationService.createNotification(notification);
                                }
                            });
                    });
            });
    }

    @Override
    public Flux<Comment> getComments(UUID postId, Pageable pageable) {
        LOG.debug("Request to get Comments for Post : {}", postId);
        return commentRepository.findByPostId(postId, pageable);
    }

    @Override
    public Mono<Void> deleteComment(UUID postId, UUID commentId) {
        LOG.debug("Request to delete Comment : {} from Post : {}", commentId, postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setCommentId(commentId);
        return commentRepository.delete(comment);
    }
}
