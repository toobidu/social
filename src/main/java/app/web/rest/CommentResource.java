package app.web.rest;

import app.domain.Comment;
import app.service.interfaces.CommentService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing Comments.
 */
@RestController
@RequestMapping("/api")
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);

    private final CommentService commentService;

    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * {@code POST  /comments} : Add a comment.
     *
     * @param comment the comment to add.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new comment.
     */
    @PostMapping("/comments")
    public Mono<ResponseEntity<Comment>> addComment(@RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to add Comment : {}", comment);
        if (comment.getCommentId() != null) {
            return Mono.error(new IllegalArgumentException("A new comment cannot already have an ID"));
        }
        return commentService
            .addComment(comment)
            .map(result -> ResponseEntity.created(URI.create("/api/comments/" + result.getCommentId())).body(result));
    }

    /**
     * {@code GET  /posts/:postId/comments} : Get comments for a post.
     *
     * @param postId the post id.
     * @param pageable the pagination information.
     * @return the {@link Flux} of comments.
     */
    @GetMapping("/posts/{postId}/comments")
    public Flux<Comment> getComments(@PathVariable UUID postId, Pageable pageable) {
        log.debug("REST request to get Comments for Post : {}", postId);
        return commentService.getComments(postId, pageable);
    }

    /**
     * {@code DELETE  /posts/:postId/comments/:commentId} : Delete a comment.
     *
     * @param postId the post id.
     * @param commentId the comment id.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        log.debug("REST request to delete Comment : {} from Post : {}", commentId, postId);
        return commentService.deleteComment(postId, commentId).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
