package app.repository.search;

import app.domain.Post;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository tìm kiếm Post trên Elasticsearch.
 */
@Repository
public interface PostSearchRepository extends ReactiveElasticsearchRepository<Post, UUID> {}
