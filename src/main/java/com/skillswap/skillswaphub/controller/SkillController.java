package com.skillswap.skillswaphub.controller;

import com.skillswap.skillswaphub.dto.SkillRequestDTO;
import com.skillswap.skillswaphub.entities.Skill;
import com.skillswap.skillswaphub.security.UserPrincipal;
import com.skillswap.skillswaphub.service.skill_service.SkillService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private SkillService skillService;
    SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createSkill(@Valid @RequestBody SkillRequestDTO skillRequest,
                                         Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Note: In real implementation, you'd get user ID from UserDetails
            // For now, we'll assume the UserDetails contains the user ID
            Long userId = getUserIdFromAuthentication(authentication);

            Skill skill = skillService.createSkill(userId, skillRequest.getTitle(), skillRequest.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(skill);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error creating skill: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Skill> skills = skillService.getAllSkillsWithPagination(pageable);
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching skills: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSkillById(@PathVariable Long id) {
        try {
            Optional<Skill> skill = skillService.getSkillById(id);
            if (skill.isPresent()) {
                return ResponseEntity.ok(skill.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching skill: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSkills(@RequestParam String keyword) {
        try {
            List<Skill> skills = skillService.searchSkills(keyword);
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error searching skills: " + e.getMessage()));
        }
    }

    @GetMapping("/my-skills")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMySkills(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<Skill> skills = skillService.getSkillsByUserId(userId);
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching your skills: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateSkill(@PathVariable Long id,
                                         @Valid @RequestBody SkillRequestDTO skillRequest,
                                         Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            Skill updatedSkill = skillService.updateSkill(id, userId, skillRequest.getTitle(), skillRequest.getDescription());
            return ResponseEntity.ok(updatedSkill);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error updating skill: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            skillService.deleteSkill(id, userId);
            return ResponseEntity.ok(new MessageResponse("Skill deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error deleting skill: " + e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentSkills() {
        try {
            List<Skill> skills = skillService.getRecentSkills();
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching recent skills: " + e.getMessage()));
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
// Message Response class for error handling
class MessageResponse {
    private String message;

}
