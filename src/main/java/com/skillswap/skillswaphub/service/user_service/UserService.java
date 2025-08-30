package com.skillswap.skillswaphub.service.user_service;

import com.skillswap.skillswaphub.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, String password);
    User updateUser(Long id, String name, String email);
    void deleteUser(Long id);
    User getUserWithSkills(Long id);
    List<User> searchUsersByName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> getAllUsers();
    boolean existsByEmail(String email);
    boolean validatePassword(String rawPassword, String encodedPassword);

}
