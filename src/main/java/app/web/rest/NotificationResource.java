package app.web.rest;

import app.domain.Notification;
import app.repository.UserRepository;
import app.security.SecurityUtils;
import app.service.interfaces.NotificationService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link Notification}.
 */
@RestController
@RequestMapping("/api")
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationResource(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /**
     * {@code GET  /notifications} : get all notifications for the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications")
    public Flux<Notification> getUserNotifications() {
        log.debug("REST request to get Notifications for current user");
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMapMany(user -> notificationService.getUserNotifications(UUID.fromString(user.getId())));
    }

    /**
     * {@code PUT  /notifications/:id/read} : mark a notification as read.
     *
     * @param id the id of the notification to mark as read.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notification.
     */
    @PutMapping("/notifications/{id}/read")
    public Mono<ResponseEntity<Notification>> markAsRead(@PathVariable UUID id) {
        log.debug("REST request to mark Notification as read : {}", id);
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> notificationService.markAsRead(UUID.fromString(user.getId()), id))
            .map(ResponseEntity::ok);
    }
}
