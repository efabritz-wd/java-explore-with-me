package ru.practicum.events;


import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.requests.EventRequestStatusUpdateRequest;
import ru.practicum.requests.EventRequestStatusUpdateResult;
import ru.practicum.requests.UpdateEventAdminRequest;
import ru.practicum.requests.UpdateEventUserRequest;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllFilteredEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateFilteredEvent(Long eventId, UpdateEventAdminRequest eventUpdateDto);

    List<EventShortDto> getAllPublicFilteredEvents(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   EventSort sort,
                                                   Integer from,
                                                   Integer size, HttpServletRequest request);

    EventFullDto getPublicFilteredEventById(Long userId, HttpServletRequest request);

    List<EventShortDto> getAllUserEvents(Long userId,
                                         Integer from,
                                         Integer size);

    EventFullDto getEventByUserAndId(Long userId, Long eventId);

    EventFullDto createEvent(Long userId,
                             NewEventDto newEventDto);

    ParticipationRequestDto getParticipantRequestByUserAndEventIds(Long userId, Long eventId);

    EventFullDto updateEventByUserAndEventIds(Long userId, Long eventId, UpdateEventUserRequest newEventDto);

    EventRequestStatusUpdateResult updateEventStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
