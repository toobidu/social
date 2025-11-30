package app.repository;

import app.domain.Friend;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository quản lý Friend trên Cassandra.
 */
@Repository
public interface FriendRepository extends ReactiveCassandraRepository<Friend, UUID> {
    Flux<Friend> findByUserId(UUID userId);
}
