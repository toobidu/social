package app.repository;

import app.domain.Post;
import java.util.UUID;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository quản lý Post trên Cassandra.
 */
@Repository
public interface PostRepository extends ReactiveCassandraRepository<Post, UUID> {}
