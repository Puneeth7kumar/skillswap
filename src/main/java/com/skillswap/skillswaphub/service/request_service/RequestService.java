package com.skillswap.skillswaphub.service.request_service;

import com.skillswap.skillswaphub.entities.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Request createRequest(Long skillId, Long requesterId, String message);
    List<Request> getRequestsByUserId(Long userId);
    List<Request> getRequestsForUserSkills(Long userId);
    List<Request> getPendingRequestsForUserSkills(Long userId);
    List<Request> getAcceptedRequestsByRequester(Long userId);
    List<Request> getRequestsBySkillId(Long skillId);
    Optional<Request> getRequestById(Long requestId);
    Request updateRequestStatus(Long requestId, Long skillOwnerId, Request.RequestStatus status);
    void deleteRequest(Long requestId, Long userId);
    List<Request> getRequestsByStatus(Request.RequestStatus status);
    long getRequestCountByUserAndStatus(Long userId, Request.RequestStatus status);
    long getRequestsMadeByUser(Long userId);
    boolean hasUserRequestedSkill(Long skillId, Long userId);
}
