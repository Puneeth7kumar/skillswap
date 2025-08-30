package com.skillswap.skillswaphub.controller;

import com.skillswap.skillswaphub.entities.Notification;
import com.skillswap.skillswaphub.repository.NotificationRepository;
import com.skillswap.skillswaphub.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER')")
    public List<Notification> getNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Cast to your custom UserPrincipal class
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId(); // return actual logged-in user ID
        } else {
            throw new RuntimeException("User not authenticated or invalid principal");
        }
    }
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void clearNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        notificationRepository.deleteByUserId(userId);
    }


}
