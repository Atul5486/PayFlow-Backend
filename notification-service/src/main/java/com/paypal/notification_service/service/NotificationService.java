package com.paypal.notification_service.service;

import com.paypal.notification_service.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(Notification notification);
    List<Notification> getNotificationsByUserId(int userId);
}
