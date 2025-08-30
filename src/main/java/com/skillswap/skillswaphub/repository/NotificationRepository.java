package com.skillswap.skillswaphub.repository;

import com.skillswap.skillswaphub.entities.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
