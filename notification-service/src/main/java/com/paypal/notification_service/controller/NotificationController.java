package com.paypal.notification_service.controller;

import com.paypal.notification_service.entity.Notification;
import com.paypal.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService=notificationService;
    }

    @PostMapping
    public Notification sendNotification(@RequestBody Notification notification){
        return  notificationService.sendNotification(notification);
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotificationByUserId(@PathVariable int userId){
       return notificationService.getNotificationsByUserId(userId);
    }


}
