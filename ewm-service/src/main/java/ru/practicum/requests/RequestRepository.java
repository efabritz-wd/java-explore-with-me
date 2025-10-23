package ru.practicum.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester(Long requester);

    @Query("SELECT r FROM Request r WHERE r.event = :eventId AND r.requester = :userId")
    Optional<Request> getRequestByEventAndRequester(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("SELECT r FROM Request r " +
            "JOIN ru.practicum.events.Event e ON r.event = e.id " +
            "WHERE r.event = :eventId AND e.initiator.id = :initiatorId")
    List<Request> findAllByEventAndInitiator(@Param("initiatorId") Long initiatorId,
                                             @Param("eventId") Long eventId);

    Optional<Request> findByIdAndRequester(Long id, Long requester);

    List<Request> findAllByEvent(Long eventId);
}
