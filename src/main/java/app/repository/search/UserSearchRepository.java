package app.repository.search;

import app.domain.User;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository tìm kiếm User trên Elasticsearch.
 */
@Repository
public interface UserSearchRepository extends ReactiveElasticsearchRepository<User, String> {}
