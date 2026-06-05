package com.paypal.wallet_service.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int walletId;

    @Column(nullable = false)
    private String type; // CREDIT, DEBIT, HOLD, RELEASE, CAPTURE

    @Column(nullable = false)
    private Long amount; // stored in paise/cents

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED, EXPIRED

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Transaction() {}

    public Transaction(int walletId, String type, Long amount, String status) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}