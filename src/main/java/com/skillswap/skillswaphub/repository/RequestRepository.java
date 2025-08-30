package com.skillswap.skillswaphub.repository;

import com.skillswap.skillswaphub.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    // Find requests by requester (user who made the request)
    @Query("SELECT r FROM Request r JOIN FETCH r.skill s JOIN FETCH s.user WHERE r.requester.id = :userId")
    List<Request> findByRequesterId(@Param("userId") Long userId);

    // Find requests for skills owned by a user (requests received)
    @Query("SELECT r FROM Request r JOIN FETCH r.skill s JOIN FETCH r.requester WHERE s.user.id = :userId")
    List<Request> findRequestsForUserSkills(@Param("userId") Long userId);

    // Find requests by status
    @Query("SELECT r FROM Request r JOIN FETCH r.skill s JOIN FETCH s.user JOIN FETCH r.requester WHERE r.status = :status")
    List<Request> findByStatus(@Param("status") Request.RequestStatus status);

    // Find requests by skill
    @Query("SELECT r FROM Request r JOIN FETCH r.requester WHERE r.skill.id = :skillId")
    List<Request> findBySkillId(@Param("skillId") Long skillId);

    // Check if request already exists
    Optional<Request> findBySkillIdAndRequesterId(Long skillId, Long requesterId);

    // Find pending requests for a user's skills
    @Query("SELECT r FROM Request r JOIN FETCH r.skill s JOIN FETCH r.requester " +
            "WHERE s.user.id = :userId AND r.status = 'PENDING'")
    List<Request> findPendingRequestsForUserSkills(@Param("userId") Long userId);

    // Find accepted requests by requester
    @Query("SELECT r FROM Request r JOIN FETCH r.skill s JOIN FETCH s.user " +
            "WHERE r.requester.id = :userId AND r.status = 'ACCEPTED'")
    List<Request> findAcceptedRequestsByRequester(@Param("userId") Long userId);

    // Count requests by status for a user's skills
    @Query("SELECT COUNT(r) FROM Request r JOIN r.skill s WHERE s.user.id = :userId AND r.status = :status")
    long countBySkillOwnerAndStatus(@Param("userId") Long userId, @Param("status") Request.RequestStatus status);

    long countByRequesterId(Long requesterId);
}
