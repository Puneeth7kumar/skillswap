package com.skillswap.skillswaphub.service.request_service;

import com.skillswap.skillswaphub.entities.Request;
import com.skillswap.skillswaphub.entities.Skill;
import com.skillswap.skillswaphub.entities.User;
import com.skillswap.skillswaphub.repository.RequestRepository;
import com.skillswap.skillswaphub.repository.SkillRepository;
import com.skillswap.skillswaphub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public RequestServiceImpl(RequestRepository requestRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public Request createRequest(Long skillId, Long requesterId, String message) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + requesterId));

        // Check if user is trying to request their own skill
        if (skill.getUser().getId().equals(requesterId)) {
            throw new RuntimeException("You cannot request your own skill");
        }

        // Check if request already exists
        if (requestRepository.findBySkillIdAndRequesterId(skillId, requesterId).isPresent()) {
            throw new RuntimeException("You have already requested this skill");
        }

        Request request = new Request();
        request.setSkill(skill);
        request.setRequester(requester);
        request.setMessage(message);
        request.setStatus(Request.RequestStatus.PENDING);

        return requestRepository.save(request);
    }

    public List<Request> getRequestsByUserId(Long userId) {
        return requestRepository.findByRequesterId(userId);
    }

    public List<Request> getRequestsForUserSkills(Long userId) {
        return requestRepository.findRequestsForUserSkills(userId);
    }

    public List<Request> getPendingRequestsForUserSkills(Long userId) {
        return requestRepository.findPendingRequestsForUserSkills(userId);
    }

    public List<Request> getAcceptedRequestsByRequester(Long userId) {
        return requestRepository.findAcceptedRequestsByRequester(userId);
    }

    public List<Request> getRequestsBySkillId(Long skillId) {
        return requestRepository.findBySkillId(skillId);
    }

    public Optional<Request> getRequestById(Long requestId) {
        return requestRepository.findById(requestId);
    }

    public Request updateRequestStatus(Long requestId, Long skillOwnerId, Request.RequestStatus status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        // Check if the user owns the skill for this request
        if (!request.getSkill().getUser().getId().equals(skillOwnerId)) {
            throw new RuntimeException("You don't have permission to update this request");
        }

        request.setStatus(status);
        return requestRepository.save(request);
    }

    public void deleteRequest(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        // Check if the user is the requester or skill owner
        boolean isRequester = request.getRequester().getId().equals(userId);
        boolean isSkillOwner = request.getSkill().getUser().getId().equals(userId);

        if (!isRequester && !isSkillOwner) {
            throw new RuntimeException("You don't have permission to delete this request");
        }

        requestRepository.delete(request);
    }

    public List<Request> getRequestsByStatus(Request.RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public long getRequestCountByUserAndStatus(Long userId, Request.RequestStatus status) {
        return requestRepository.countBySkillOwnerAndStatus(userId, status);
    }

    public long getRequestsMadeByUser(Long userId) {
        return requestRepository.countByRequesterId(userId);
    }

    public boolean hasUserRequestedSkill(Long skillId, Long userId) {
        return requestRepository.findBySkillIdAndRequesterId(skillId, userId).isPresent();
    }
}
