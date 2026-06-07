package com.product_service.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class ProductDto {

    String name;

    MultipartFile image;

    int points;

    int userId;

    String category;

    String description;
}
