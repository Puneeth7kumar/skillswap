package com.skillswap.skillswaphub.controller;

import com.skillswap.skillswaphub.dto.NotificationDTO;
import com.skillswap.skillswaphub.dto.RequestCreateDto;
import com.skillswap.skillswaphub.dto.RequestStatusDto;
import com.skillswap.skillswaphub.entities.Notification;
import com.skillswap.skillswaphub.entities.Request;
import com.skillswap.skillswaphub.entities.Skill;
import com.skillswap.skillswaphub.entities.User;
import com.skillswap.skillswaphub.repository.NotificationRepository;
import com.skillswap.skillswaphub.repository.RequestRepository;
import com.skillswap.skillswaphub.repository.SkillRepository;
import com.skillswap.skillswaphub.repository.UserRepository;
import com.skillswap.skillswaphub.security.UserPrincipal;
import com.skillswap.skillswaphub.service.request_service.RequestService;
import com.skillswap.skillswaphub.service.skill_service.SkillService;
import com.skillswap.skillswaphub.service.user_service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final UserService userService;
    private final SkillService skillService;
    private RequestService requestService;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final NotificationRepository notificationRepository;
    RequestController(RequestService requestService, SkillRepository skillRepository, UserRepository userRepository, RequestRepository requestRepository, SimpMessagingTemplate messagingTemplate, UserService userService, SkillService skillService, NotificationRepository notificationRepository) {
        this.requestService = requestService;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.skillService = skillService;
    }
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRequest(@Valid @RequestBody RequestCreateDto requestDto,
                                           Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            Request request = requestService.createRequest(
                    requestDto.getSkillId(),
                    userId,
                    requestDto.getMessage()
            );
            User requester=userService.getUserWithSkills(userId);
            Skill skill=skillService.getSkillById(requestDto.getSkillId()).orElseThrow(()->new RuntimeException("Skill not found"));
            Notification notification = new Notification();
            notification.setUserId(skill.getUser().getId());
            notification.setMessage(requester.getName() + " requested to learn " + skill.getTitle());
            notificationRepository.save(notification);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error creating request: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserRequests(@PathVariable Long userId, Authentication authentication) {
        try {
            // Verify user can only access their own requests
            Long currentUserId = getUserIdFromAuthentication(authentication);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied: You can only view your own requests"));
            }

            List<Request> requests = requestService.getRequestsByUserId(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching requests: " + e.getMessage()));
        }
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getReceivedRequests(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Request> requests = requestService.getRequestsForUserSkills(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching received requests: " + e.getMessage()));
        }
    }

    @GetMapping("/received/pending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPendingReceivedRequests(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Request> requests = requestService.getPendingRequestsForUserSkills(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching pending requests: " + e.getMessage()));
        }
    }

    @GetMapping("/made")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMadeRequests(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Request> requests = requestService.getRequestsByUserId(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching made requests: " + e.getMessage()));
        }
    }

    @GetMapping("/accepted")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAcceptedRequests(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Request> requests = requestService.getAcceptedRequestsByRequester(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching accepted requests: " + e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long requestId,
                                                 @Valid @RequestBody RequestStatusDto statusDto,
                                                 Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            Request.RequestStatus status;

            switch (statusDto.getStatus().toLowerCase()) {
                case "accepted":
                    status = Request.RequestStatus.ACCEPTED;
                    break;
                case "rejected":
                    status = Request.RequestStatus.REJECTED;
                    break;
                case "pending":
                    status = Request.RequestStatus.PENDING;
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Invalid status. Use: accepted, rejected, or pending"));
            }

            Request updatedRequest = requestService.updateRequestStatus(requestId, userId, status);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error updating request status: " + e.getMessage()));
        }
    }

    @GetMapping("/skill/{skillId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRequestsForSkill(@PathVariable Long skillId) {
        try {
            List<Request> requests = requestService.getRequestsBySkillId(skillId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching requests for skill: " + e.getMessage()));
        }
    }

    @GetMapping("/details/{requestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRequestDetails(@PathVariable Long requestId, Authentication authentication) {
        try {
            Optional<Request> request = requestService.getRequestById(requestId);
            if (request.isPresent()) {
                Long userId = getUserIdFromAuthentication(authentication);
                Request req = request.get();

                // Check if user has permission to view this request
                boolean isRequester = req.getRequester().getId().equals(userId);
                boolean isSkillOwner = req.getSkill().getUser().getId().equals(userId);

                if (!isRequester && !isSkillOwner) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new MessageResponse("Access denied: You don't have permission to view this request"));
                }

                return ResponseEntity.ok(req);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching request details: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            requestService.deleteRequest(requestId, userId);
            return ResponseEntity.ok(new MessageResponse("Request deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error deleting request: " + e.getMessage()));
        }
    }

    // Statistics endpoints
    @GetMapping("/stats/made")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRequestsMadeCount(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            long count = requestService.getRequestsMadeByUser(userId);
            return ResponseEntity.ok(new RequestStatsResponse("requests_made", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching request statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/pending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPendingRequestsCount(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            long count = requestService.getRequestCountByUserAndStatus(userId, Request.RequestStatus.PENDING);
            return ResponseEntity.ok(new RequestStatsResponse("pending_requests", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching pending request statistics: " + e.getMessage()));
        }
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Cast to your custom UserPrincipal class
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId(); // return actual logged-in user ID
        } else {
            throw new RuntimeException("User not authenticated or invalid principal");
        }
    }
}
@Data
@AllArgsConstructor
@NoArgsConstructor
// Response class for request statistics
class RequestStatsResponse {
    private String type;
    private long count;

}
