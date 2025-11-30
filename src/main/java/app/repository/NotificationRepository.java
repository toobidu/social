package app.repository;

import app.domain.Notification;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Cassandra repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends ReactiveCassandraRepository<Notification, UUID> {
    Flux<Notification> findByUserId(UUID userId);

    Mono<Notification> findByUserIdAndId(UUID userId, UUID id);
}
