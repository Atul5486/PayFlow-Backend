package com.product_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column
    String name;

    @Column
    int userId;

    @Column
    String image;

    @Column
    int points;

    @Column
    String category;

    @Column
    String description;

    @Column
    LocalDateTime createdAt=LocalDateTime.now();

    @Column
    LocalDateTime updatedAt;
}
