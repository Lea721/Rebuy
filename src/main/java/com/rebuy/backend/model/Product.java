package com.rebuy.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String category;

    // 👉 IMPORTANT : le frontend envoie "condition"
    // donc on utilise le même nom ici
    @Column(name = "condition")
    private String condition;

    private Double price;

    @Column(columnDefinition = "TEXT")
    private String imageBase64;

    private LocalDateTime createdAt = LocalDateTime.now();
}
