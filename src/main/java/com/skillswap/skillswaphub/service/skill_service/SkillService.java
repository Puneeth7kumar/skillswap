package com.skillswap.skillswaphub.service.skill_service;

import com.skillswap.skillswaphub.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SkillService {
    Skill createSkill(Long userId, String title, String description);
    List<Skill> getAllSkills();
    List<Skill> getSkillsByUserId(Long userId);
    List<Skill> getRecentSkills();
    Page<Skill> getAllSkillsWithPagination(Pageable pageable);
    Optional<Skill> getSkillById(Long id);
    List<Skill> searchSkillsByTitle(String title);
    List<Skill> searchSkills(String keyword);
    Skill updateSkill(Long skillId, Long userId, String title, String description);
    void deleteSkill(Long skillId, Long userId);
    long getSkillsCountByUserId(Long userId);
    boolean isSkillOwner(Long skillId, Long userId);

}
