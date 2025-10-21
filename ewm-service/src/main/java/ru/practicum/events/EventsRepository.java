package ru.practicum.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    Boolean existsByCategoryId(Long id);

    @Query("SELECT e FROM Event e WHERE e.category.id = :id")
    List<Event> findAllByCategoryId(@Param("id") Long id);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    List<Event> findPublicFilteredEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate > :date) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    List<Event> findPublicFilteredEventsAfterNow(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("date") LocalDateTime date,
            @Param("onlyAvailable") boolean onlyAvailable,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> getAllFilteredEvents(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

}