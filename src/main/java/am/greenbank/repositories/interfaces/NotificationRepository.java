package am.greenbank.repositories.interfaces;

import am.greenbank.entities.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Optional<Notification> findById(String id);

    List<Notification> findAll();

    List<Notification> findAllByUserId(String userId);

    List<Notification> findAllByUserIdAndRead(String userId, boolean read);

    Notification save(Notification notification);
}
