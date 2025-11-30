package app.web.rest;

import app.criteria.dto.PostSearchCriteria;
import app.domain.NewsFeed;
import app.domain.Post;
import app.domain.UserPost;
import app.service.interfaces.PostService;
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
 * REST controller for managing {@link app.domain.Post}.
 */
@RestController
@RequestMapping("/api")
public class PostResource {

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    /**
     * {@code POST  /posts} : Create a new post.
     *
     * @param post the post to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new post.
     */
    @PostMapping("/posts")
    public Mono<ResponseEntity<Post>> createPost(@RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to save Post : {}", post);
        if (post.getPostId() != null) {
            return Mono.error(new IllegalArgumentException("A new post cannot already have an ID"));
        }
        return postService
            .createPost(post)
            .map(result -> ResponseEntity.created(URI.create("/api/posts/" + result.getPostId())).body(result));
    }

    /**
     * {@code PUT  /posts/:id} : Updates an existing post.
     *
     * @param id the id of the post to save.
     * @param post the post to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post.
     */
    @PutMapping("/posts/{id}")
    public Mono<ResponseEntity<Post>> updatePost(@PathVariable(value = "id", required = false) final UUID id, @RequestBody Post post) {
        log.debug("REST request to update Post : {}, {}", id, post);
        if (post.getPostId() == null) {
            return Mono.error(new IllegalArgumentException("Invalid id"));
        }
        if (!post.getPostId().equals(id)) {
            return Mono.error(new IllegalArgumentException("Invalid ID"));
        }

        return postService.updatePost(post).map(ResponseEntity::ok);
    }

    /**
     * {@code GET  /posts/:id} : Get the "id" post.
     *
     * @param id the id of the post to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the post, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/posts/{id}")
    public Mono<ResponseEntity<Post>> getPost(@PathVariable UUID id) {
        log.debug("REST request to get Post : {}", id);
        return postService.findOne(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /posts/:id} : Delete the "id" post.
     *
     * @param id the id of the post to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posts/{id}")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable UUID id) {
        log.debug("REST request to delete Post : {}", id);
        return postService.delete(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * {@code GET  /posts/search} : Search for posts using criteria.
     *
     * @param criteria the search criteria.
     * @param pageable the pagination information.
     * @return the {@link Flux} of posts.
     */
    @GetMapping("/posts/search")
    public Flux<Post> search(PostSearchCriteria criteria, Pageable pageable) {
        log.debug("REST request to search Posts by criteria: {}", criteria);
        return postService.search(criteria, pageable);
    }

    /**
     * {@code GET  /news-feed} : Get news feed for current user.
     *
     * @param userId the user id (should be from security context in real app).
     * @param pageable the pagination information.
     * @return the {@link Flux} of news feed items.
     */
    @GetMapping("/news-feed")
    public Flux<NewsFeed> getNewsFeed(@RequestParam UUID userId, Pageable pageable) {
        log.debug("REST request to get NewsFeed for user: {}", userId);
        return postService.getNewsFeed(userId, pageable);
    }

    /**
     * {@code GET  /users/:userId/timeline} : Get timeline for a user.
     *
     * @param userId the user id.
     * @param pageable the pagination information.
     * @return the {@link Flux} of user post items.
     */
    @GetMapping("/users/{userId}/timeline")
    public Flux<UserPost> getUserTimeline(@PathVariable UUID userId, Pageable pageable) {
        log.debug("REST request to get Timeline for user: {}", userId);
        return postService.getUserTimeline(userId, pageable);
    }
}
