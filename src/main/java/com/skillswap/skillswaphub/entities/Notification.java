package com.skillswap.skillswaphub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // receiver
    private String message;
    @Column(name = "is_read", nullable = false)
    private boolean read;

    private LocalDateTime createdAt = LocalDateTime.now();

}

