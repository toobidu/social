package app.repository;

import app.domain.User;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.data.cassandra.ReactiveResultSet;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Cassandra reactive repository for the {@link User} entity.
 */
@Repository
public class UserRepository {

    private final ReactiveSession session;

    private final Validator validator;

    private final ReactiveCassandraTemplate cqlTemplate;

    private PreparedStatement findAllStmt;

    private PreparedStatement findOneByActivationKeyStmt;

    private PreparedStatement findOneByResetKeyStmt;

    private PreparedStatement insertByActivationKeyStmt;

    private PreparedStatement insertByResetKeyStmt;

    private PreparedStatement deleteByIdStmt;

    private PreparedStatement deleteByActivationKeyStmt;

    private PreparedStatement deleteByResetKeyStmt;

    private PreparedStatement findOneByLoginStmt;

    private PreparedStatement insertByLoginStmt;

    private PreparedStatement deleteByLoginStmt;

    private PreparedStatement findOneByEmailStmt;

    private PreparedStatement insertByEmailStmt;

    private PreparedStatement deleteByEmailStmt;

    private PreparedStatement truncateStmt;

    private PreparedStatement truncateByResetKeyStmt;

    private PreparedStatement truncateByLoginStmt;

    private PreparedStatement truncateByEmailStmt;

    public UserRepository(ReactiveCassandraTemplate cqlTemplate, ReactiveSession session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.cqlTemplate = cqlTemplate;

        findAllStmt = session.prepare("SELECT * FROM user").block();

        findOneByActivationKeyStmt = session
            .prepare("SELECT id " + "FROM user_by_activation_key " + "WHERE activation_key = :activation_key")
            .block();

        findOneByResetKeyStmt = session.prepare("SELECT id " + "FROM user_by_reset_key " + "WHERE reset_key = :reset_key").block();

        insertByActivationKeyStmt = session
            .prepare("INSERT INTO user_by_activation_key (activation_key, id) " + "VALUES (:activation_key, :id)")
            .block();

        insertByResetKeyStmt = session.prepare("INSERT INTO user_by_reset_key (reset_key, id) " + "VALUES (:reset_key, :id)").block();

        deleteByIdStmt = session.prepare("DELETE FROM user " + "WHERE id = :id").block();

        deleteByActivationKeyStmt = session
            .prepare("DELETE FROM user_by_activation_key " + "WHERE activation_key = :activation_key")
            .block();

        deleteByResetKeyStmt = session.prepare("DELETE FROM user_by_reset_key " + "WHERE reset_key = :reset_key").block();

        findOneByLoginStmt = session.prepare("SELECT id " + "FROM user_by_login " + "WHERE login = :login").block();

        insertByLoginStmt = session.prepare("INSERT INTO user_by_login (login, id) " + "VALUES (:login, :id)").block();

        deleteByLoginStmt = session.prepare("DELETE FROM user_by_login " + "WHERE login = :login").block();

        findOneByEmailStmt = session.prepare("SELECT id " + "FROM user_by_email " + "WHERE email     = :email").block();

        insertByEmailStmt = session.prepare("INSERT INTO user_by_email (email, id) " + "VALUES (:email, :id)").block();

        deleteByEmailStmt = session.prepare("DELETE FROM user_by_email " + "WHERE email = :email").block();

        truncateStmt = session.prepare("TRUNCATE user").block();

        truncateByResetKeyStmt = session.prepare("TRUNCATE user_by_reset_key").block();

        truncateByLoginStmt = session.prepare("TRUNCATE user_by_login").block();

        truncateByEmailStmt = session.prepare("TRUNCATE user_by_email").block();
    }

    public Mono<User> findById(String id) {
        return cqlTemplate
            .selectOneById(id, User.class)
            .map(user -> {
                if (user.getAuthorities() == null) {
                    user.setAuthorities(new HashSet<>());
                }
                return user;
            });
    }

    public Mono<User> findOneByActivationKey(String activationKey) {
        BoundStatement stmt = findOneByActivationKeyStmt.bind().setString("activation_key", activationKey);
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByResetKey(String resetKey) {
        BoundStatement stmt = findOneByResetKeyStmt.bind().setString("reset_key", resetKey);
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByEmailIgnoreCase(String email) {
        BoundStatement stmt = findOneByEmailStmt.bind().setString("email", email.toLowerCase());
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByLogin(String login) {
        BoundStatement stmt = findOneByLoginStmt.bind().setString("login", login);
        return findOneFromIndex(stmt);
    }

    public Flux<User> findAll() {
        return cqlTemplate
            .select(findAllStmt.bind(), User.class)
            .map(user -> {
                if (user.getAuthorities() == null) {
                    user.setAuthorities(new HashSet<>());
                }
                return user;
            });
    }

    public Mono<User> save(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return this.findById(user.getId())
            .flatMapMany(oldUser -> {
                Flux<ReactiveResultSet> deleteOps = Flux.empty();
                if (!StringUtils.isEmpty(oldUser.getActivationKey()) && !oldUser.getActivationKey().equals(user.getActivationKey())) {
                    deleteOps.mergeWith(
                        session.execute(deleteByActivationKeyStmt.bind().setString("activation_key", oldUser.getActivationKey()))
                    );
                }
                if (!StringUtils.isEmpty(oldUser.getResetKey()) && !oldUser.getResetKey().equals(user.getResetKey())) {
                    deleteOps.mergeWith(session.execute(deleteByResetKeyStmt.bind().setString("reset_key", oldUser.getResetKey())));
                }
                if (!StringUtils.isEmpty(oldUser.getLogin()) && !oldUser.getLogin().equals(user.getLogin())) {
                    deleteOps.mergeWith(session.execute(deleteByLoginStmt.bind().setString("login", oldUser.getLogin())));
                }
                if (!StringUtils.isEmpty(oldUser.getEmail()) && !oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                    deleteOps.mergeWith(session.execute(deleteByEmailStmt.bind().setString("email", oldUser.getEmail().toLowerCase())));
                }
                return deleteOps;
            })
            .then(
                Mono.defer(() -> {
                    BatchStatementBuilder batch = BatchStatement.builder(DefaultBatchType.LOGGED);
                    batch.addStatement(getInsertStatement(user));
                    if (!StringUtils.isEmpty(user.getActivationKey())) {
                        batch.addStatement(
                            insertByActivationKeyStmt
                                .bind()
                                .setString("activation_key", user.getActivationKey())
                                .setString("id", user.getId())
                        );
                    }
                    if (!StringUtils.isEmpty(user.getResetKey())) {
                        batch.addStatement(
                            insertByResetKeyStmt.bind().setString("reset_key", user.getResetKey()).setString("id", user.getId())
                        );
                    }
                    batch.addStatement(insertByLoginStmt.bind().setString("login", user.getLogin()).setString("id", user.getId()));
                    batch.addStatement(
                        insertByEmailStmt.bind().setString("email", user.getEmail().toLowerCase()).setString("id", user.getId())
                    );
                    return session.execute(batch.build());
                })
            )
            .thenReturn(user);
    }

    private SimpleStatement getInsertStatement(User user) {
        CassandraConverter converter = cqlTemplate.getConverter();
        CassandraPersistentEntity<?> persistentEntity = converter.getMappingContext().getRequiredPersistentEntity(user.getClass());
        Map<CqlIdentifier, Object> toInsert = new LinkedHashMap<>();
        converter.write(user, toInsert, persistentEntity);
        RegularInsert insert = QueryBuilder.insertInto(persistentEntity.getTableName()).value("id", QueryBuilder.literal(user.getId()));
        for (Map.Entry<CqlIdentifier, Object> entry : toInsert.entrySet()) {
            insert = insert.value(entry.getKey(), QueryBuilder.literal(entry.getValue()));
        }
        return insert.build();
    }

    public Mono<Void> delete(User user) {
        BatchStatementBuilder batch = BatchStatement.builder(DefaultBatchType.LOGGED);
        batch.addStatement(deleteByIdStmt.bind().setString("id", user.getId()));
        if (!StringUtils.isEmpty(user.getActivationKey())) {
            batch.addStatement(deleteByActivationKeyStmt.bind().setString("activation_key", user.getActivationKey()));
        }
        if (!StringUtils.isEmpty(user.getResetKey())) {
            batch.addStatement(deleteByResetKeyStmt.bind().setString("reset_key", user.getResetKey()));
        }
        batch.addStatement(deleteByLoginStmt.bind().setString("login", user.getLogin()));
        batch.addStatement(deleteByEmailStmt.bind().setString("email", user.getEmail().toLowerCase()));
        return session.execute(batch.build()).then();
    }

    private Mono<User> findOneFromIndex(BoundStatement stmt) {
        return session.execute(stmt).flatMap(rs -> rs.rows().next()).map(row -> row.getString("id")).flatMap(this::findById);
    }

    public Mono<Void> deleteAll() {
        return Flux.just(truncateStmt, truncateByEmailStmt, truncateByLoginStmt, truncateByResetKeyStmt)
            .map(PreparedStatement::bind)
            .flatMap(session::execute)
            .then();
    }
}
