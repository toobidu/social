package app.service.interfaces;

import app.domain.User;
import app.service.dto.AdminUserDTO;
import app.service.dto.UserDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing users.
 */
public interface UserService {
    Mono<User> activateRegistration(String key);

    Mono<User> completePasswordReset(String newPassword, String key);

    Mono<User> requestPasswordReset(String mail);

    Mono<User> registerUser(AdminUserDTO userDTO, String password);

    Mono<User> createUser(AdminUserDTO userDTO);

    Mono<AdminUserDTO> updateUser(AdminUserDTO userDTO);

    Mono<Void> deleteUser(String login);

    Mono<Void> updateAccount(AdminUserDTO userDTO);

    Mono<Void> changePassword(String currentClearTextPassword, String newPassword);

    Flux<AdminUserDTO> getAllManagedUsers();

    Flux<UserDTO> getAllPublicUsers();

    Mono<User> getUserWithAuthoritiesByLogin(String login);

    Mono<User> getUserWithAuthorities();
}
