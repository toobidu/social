package app.service.interfaces;

import app.domain.Notification;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Notification}.
 */
public interface NotificationService {
    /**
     * Create a notification asynchronously.
     *
     * @param notification the notification to create.
     */
    void createNotification(Notification notification);

    /**
     * Get all notifications for a user.
     *
     * @param userId the user id.
     * @return the list of notifications.
     */
    Flux<Notification> getUserNotifications(UUID userId);

    /**
     * Mark a notification as read.
     *
     * @param userId the user id.
     * @param notificationId the notification id.
     * @return the updated notification.
     */
    Mono<Notification> markAsRead(UUID userId, UUID notificationId);
}
