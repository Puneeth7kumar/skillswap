package com.skillswap.skillswaphub.repository;

import com.skillswap.skillswaphub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.skills WHERE u.id = :id")
    Optional<User> findByIdWithSkills(@Param("id") Long id);
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    java.util.List<User> findByNameContainingIgnoreCase(@Param("name") String name);
}
