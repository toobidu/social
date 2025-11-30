package app.service.interfaces;

import app.criteria.dto.PostSearchCriteria;
import app.domain.NewsFeed;
import app.domain.Post;
import app.domain.UserPost;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Post}.
 */
public interface PostService {
    /**
     * Create a new post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    Mono<Post> createPost(Post post);

    /**
     * Update an existing post.
     *
     * @param post the entity to update.
     * @return the persisted entity.
     */
    Mono<Post> updatePost(Post post);

    /**
     * Get all the posts.
     *
     * @return the list of entities.
     */
    Flux<Post> findAll();

    /**
     * Get the "id" post.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Post> findOne(UUID id);

    /**
     * Delete the "id" post.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(UUID id);

    /**
     * Search for the post corresponding to the query.
     *
     * @param criteria the search criteria.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Post> search(PostSearchCriteria criteria, Pageable pageable);

    /**
     * Get news feed for a user.
     *
     * @param userId the user id.
     * @param pageable the pagination information.
     * @return the list of news feed items.
     */
    Flux<NewsFeed> getNewsFeed(UUID userId, Pageable pageable);

    /**
     * Get user timeline.
     *
     * @param userId the user id.
     * @param pageable the pagination information.
     * @return the list of user post items.
     */
    Flux<UserPost> getUserTimeline(UUID userId, Pageable pageable);
}
