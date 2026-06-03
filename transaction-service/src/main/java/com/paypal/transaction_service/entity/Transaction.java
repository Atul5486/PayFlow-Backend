package com.paypal.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sender_id", nullable = false)
    private int senderId;

    @Column(name="receiver_id", nullable = false)
    private int receiverId;

    @Column(nullable = false)
    @Positive(message="Amount must be positive")
    private  Double amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status;

    public Transaction() {
    }

    @PrePersist
    public void prePersist(){
        if(timestamp==null){
            timestamp=LocalDateTime.now();
        }
        if(status==null){
            status="PENDING";
        }
    }

    public Transaction(int senderId, int receiverId, Double amount, LocalDateTime timestamp, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "transaction{" +
                "id=" + id +
                ", senderName='" + senderId + '\'' +
                ", receiverName='" + receiverId + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
