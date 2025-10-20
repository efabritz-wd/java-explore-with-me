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

    Optional<Request> getRequestByEventAndRequester(Long event, Long requester);

    @Query("SELECT r FROM Request r " +
            "JOIN ru.practicum.events.Event e ON r.event = e.id " +
            "WHERE r.event = :eventId AND e.initiator.id = :initiatorId")
    List<Request> findAllByEventAndInitiator(@Param("initiatorId") Long initiatorId,
                                             @Param("eventId") Long eventId);

    Optional<Request> findByIdAndRequester(Long id, Long requester);
}
