package app.repository;

import app.domain.UserPost;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository quản lý UserPost (Timeline) trên Cassandra.
 */
@Repository
public interface UserPostRepository extends ReactiveCassandraRepository<UserPost, UUID> {
    Flux<UserPost> findByUserId(UUID userId, Pageable pageable);
}
