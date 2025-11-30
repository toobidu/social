package app.repository;

import app.domain.Comment;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository quản lý Comment trên Cassandra.
 */
@Repository
public interface CommentRepository extends ReactiveCassandraRepository<Comment, UUID> {
    Flux<Comment> findByPostId(UUID postId, Pageable pageable);
    Flux<Comment> findByPostId(UUID postId);
}
