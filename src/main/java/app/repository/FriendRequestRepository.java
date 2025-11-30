package app.repository;

import app.domain.FriendRequest;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository quản lý FriendRequest trên Cassandra.
 */
@Repository
public interface FriendRequestRepository extends ReactiveCassandraRepository<FriendRequest, UUID> { // UUID is not full PK, but ReactiveCassandraRepository needs a type.
    // Actually for composite key, we might need MapId or just use the entity.

    Flux<FriendRequest> findByReceiverId(UUID receiverId);

    // To find specific request: receiverId and senderId.
    // Since senderId is clustered, we can query.
    Mono<FriendRequest> findByReceiverIdAndSenderId(UUID receiverId, UUID senderId);
}
