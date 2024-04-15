package am.greenbank.services;

import am.greenbank.entities.Notification;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.NotificationNotFoundException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.repositories.interfaces.NotificationRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServie {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<Notification> getAllByUserId(String userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    public Notification readNotificationById(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
    }

    private void notifyUser(Notification savedFromNotification, String userId) {
        simpMessagingTemplate.convertAndSendToUser(userId, "/notification", savedFromNotification);
    }

    public void sendNotification(Notification... notifications) {
        Arrays.stream(notifications)
            .forEach(
                notification -> {
                    Notification savedNotification = notificationRepository.save(notification);
                    updateUserNotifications(savedNotification.getUserId(), savedNotification.getId());
                    notifyUser(savedNotification, savedNotification.getUserId());
                }
            );
    }

    private void updateUserNotifications(String userId, String notificationId) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);
        user.getNotifications().add(notificationId);
        userRepository.saveUser(user);
    }
}

