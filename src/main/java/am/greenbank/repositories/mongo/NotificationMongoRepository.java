package am.greenbank.repositories.mongo;

import am.greenbank.entities.Notification;
import am.greenbank.repositories.interfaces.NotificationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMongoRepository extends NotificationRepository, MongoRepository<Notification, String> {
}
