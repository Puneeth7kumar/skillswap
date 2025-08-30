package com.skillswap.skillswaphub.service.skill_service;

import com.skillswap.skillswaphub.entities.Skill;
import com.skillswap.skillswaphub.entities.User;
import com.skillswap.skillswaphub.repository.SkillRepository;
import com.skillswap.skillswaphub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillServiceImpl(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public Skill createSkill(Long userId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Skill skill = new Skill();
        skill.setUser(user);
        skill.setTitle(title);
        skill.setDescription(description);

        return skillRepository.save(skill);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Page<Skill> getAllSkillsWithPagination(Pageable pageable) {
        return skillRepository.findAllWithUser(pageable);
    }

    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findByIdWithUser(id);
    }

    public List<Skill> getSkillsByUserId(Long userId) {
        return skillRepository.findByUserId(userId);
    }

    public List<Skill> searchSkillsByTitle(String title) {
        return skillRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Skill> searchSkills(String keyword) {
        return skillRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword);
    }

    public List<Skill> getRecentSkills() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return skillRepository.findRecentSkills(cutoffDate);
    }

    public Skill updateSkill(Long skillId, Long userId, String title, String description) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));

        // Check if the user owns this skill
        if (!skill.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to update this skill");
        }

        skill.setTitle(title);
        skill.setDescription(description);
        return skillRepository.save(skill);
    }

    public void deleteSkill(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));

        // Check if the user owns this skill
        if (!skill.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this skill");
        }

        skillRepository.delete(skill);
    }

    public long getSkillsCountByUserId(Long userId) {
        return skillRepository.countByUserId(userId);
    }

    public boolean isSkillOwner(Long skillId, Long userId) {
        Optional<Skill> skill = skillRepository.findById(skillId);
        return skill.isPresent() && skill.get().getUser().getId().equals(userId);
    }
}
