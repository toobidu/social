package app.repository;

import app.domain.User;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Cassandra reactive repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends ReactiveCassandraRepository<User, String> {
    Mono<User> findOneByActivationKey(String activationKey);

    Mono<User> findOneByResetKey(String resetKey);

    Mono<User> findOneByEmailIgnoreCase(String email);

    Mono<User> findOneByLogin(String login);
}
