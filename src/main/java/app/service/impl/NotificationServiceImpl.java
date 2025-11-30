package app.service.impl;

import app.domain.Notification;
import app.repository.NotificationRepository;
import app.service.interfaces.NotificationService;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(Notification notification) {
        // Fire-and-forget
        Mono.defer(() -> {
            if (notification.getId() == null) {
                notification.setId(Uuids.timeBased());
            }
            if (notification.getCreatedDate() == null) {
                notification.setCreatedDate(Instant.now());
            }
            notification.setRead(false);

            LOG.debug("Creating notification: {}", notification);
            return notificationRepository.save(notification);
        }).subscribe(saved -> LOG.debug("Notification created: {}", saved.getId()), e -> LOG.error("Failed to create notification", e));
    }

    @Override
    public Flux<Notification> getUserNotifications(UUID userId) {
        LOG.debug("Request to get notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Mono<Notification> markAsRead(UUID userId, UUID notificationId) {
        LOG.debug("Request to mark notification as read: {}", notificationId);
        return notificationRepository
            .findByUserIdAndId(userId, notificationId)
            .flatMap(notification -> {
                notification.setRead(true);
                return notificationRepository.save(notification);
            });
    }
}
