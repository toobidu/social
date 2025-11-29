package app.service;

import app.domain.Post;
import app.repository.PostRepository;
import app.repository.search.PostSearchRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service quản lý bài viết (Post).
 */
@Service
public class PostService {

    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    private final PostSearchRepository postSearchRepository;

    public PostService(PostRepository postRepository, PostSearchRepository postSearchRepository) {
        this.postRepository = postRepository;
        this.postSearchRepository = postSearchRepository;
    }

    /**
     * Tạo hoặc cập nhật bài viết.
     * Đồng bộ dữ liệu sang Elasticsearch.
     *
     * @param post bài viết cần lưu.
     * @return bài viết đã lưu.
     */
    public Mono<Post> save(Post post) {
        LOG.debug("Request to save Post : {}", post);
        return postRepository.save(post).flatMap(savedPost -> postSearchRepository.save(savedPost).thenReturn(savedPost));
    }

    /**
     * Lấy tất cả bài viết.
     *
     * @return danh sách bài viết.
     */
    public Flux<Post> findAll() {
        LOG.debug("Request to get all Posts");
        return postRepository.findAll();
    }

    /**
     * Lấy bài viết theo ID.
     *
     * @param id ID bài viết.
     * @return bài viết (nếu tìm thấy).
     */
    public Mono<Post> findOne(UUID id) {
        LOG.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    /**
     * Xóa bài viết theo ID.
     * Đồng bộ xóa trên Elasticsearch.
     *
     * @param id ID bài viết.
     * @return void.
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Post : {}", id);
        return postRepository.deleteById(id).then(postSearchRepository.deleteById(id));
    }
}
