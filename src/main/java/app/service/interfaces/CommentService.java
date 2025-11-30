package app.service.interfaces;

import app.domain.Comment;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing Comments.
 */
public interface CommentService {
    /**
     * Add a comment to a post.
     *
     * @param comment the comment to add.
     * @return the saved comment.
     */
    Mono<Comment> addComment(Comment comment);

    /**
     * Get comments for a post.
     *
     * @param postId the post id.
     * @param pageable pagination info.
     * @return list of comments.
     */
    Flux<Comment> getComments(UUID postId, Pageable pageable);

    /**
     * Delete a comment.
     *
     * @param postId the post id.
     * @param commentId the comment id.
     * @return completion signal.
     */
    Mono<Void> deleteComment(UUID postId, UUID commentId);
}
