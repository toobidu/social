package app.repository;

import app.domain.NewsFeed;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository quản lý NewsFeed trên Cassandra.
 */
@Repository
public interface NewsFeedRepository extends ReactiveCassandraRepository<NewsFeed, UUID> {
    Flux<NewsFeed> findByUserId(UUID userId, Pageable pageable);

    // Slice is better for infinite scroll but Flux is fine for reactive stream
    Flux<NewsFeed> findByUserId(UUID userId);
}
