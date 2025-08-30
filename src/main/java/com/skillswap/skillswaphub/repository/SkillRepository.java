package com.skillswap.skillswaphub.repository;

import com.skillswap.skillswaphub.entities.Skill;
import com.skillswap.skillswaphub.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    // Find skills by user
    List<Skill> findByUser(User user);

    // Find skills by user ID
    List<Skill> findByUserId(Long userId);

    // Find skill by ID with user details
    @Query("SELECT s FROM Skill s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Skill> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT s FROM Skill s JOIN FETCH s.user WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Skill> findByTitleContainingIgnoreCase(@Param("title") String title);

    // Search skills by title or description (case insensitive)
    @Query("SELECT s FROM Skill s JOIN FETCH s.user WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Skill> findByTitleOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    // Get all skills with pagination (with user details)
    @Query("SELECT s FROM Skill s JOIN FETCH s.user ORDER BY s.createdAt DESC")
    Page<Skill> findAllWithUser(Pageable pageable);

    // Find recent skills (last 30 days)
    @Query("SELECT s FROM Skill s JOIN FETCH s.user " +
            "WHERE s.createdAt >= :cutoffDate " +
            "ORDER BY s.createdAt DESC")
    List<Skill> findRecentSkills(@Param("cutoffDate") LocalDateTime cutoffDate);



    // Count skills by user
    long countByUserId(Long userId);
}
