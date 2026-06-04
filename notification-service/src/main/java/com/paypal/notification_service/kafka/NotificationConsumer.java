package  com.paypal.notification_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paypal.notification_service.entity.Notification;
import com.paypal.notification_service.entity.Transaction;
import com.paypal.notification_service.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    private  final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    public NotificationConsumer(NotificationRepository notificationRepository, RestTemplate restTemplate){
        this.notificationRepository=notificationRepository;
        this.restTemplate = restTemplate;
        this.mapper=new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "txn-initiated", groupId = "notification-group")
    public void consumeTransaction(Transaction transaction) {
        Notification notification=new Notification();
        notification.setUserId(transaction.getReceiverId());

        String senderName = restTemplate.getForObject(
                "http://localhost:8081/api/users/getName/" + transaction.getSenderId(),String.class);
        System.out.println(senderName);
        notification.setMessage("₹ "+transaction.getAmount()+"  received from user "+senderName);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
        System.out.println("Notification Saved "+notification);
    }

}