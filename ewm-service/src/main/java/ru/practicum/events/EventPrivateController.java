package ru.practicum.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.requests.EventRequestStatusUpdateRequest;
import ru.practicum.requests.EventRequestStatusUpdateResult;
import ru.practicum.requests.UpdateEventUserRequest;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllUserEvents(@PathVariable("userId") Long userId,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/eventId")
    public EventFullDto getEventByUserAndId(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId) {
        return eventService.getEventByUserAndId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public ParticipationRequestDto getParticipantRequestByUserAndEventIds(@PathVariable("userId") Long userId,
                                                                          @PathVariable("eventId") Long eventId) {
        return eventService.getParticipantRequestByUserAndEventIds(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserAndEventIds(@PathVariable("userId") Long userId,
                                                               @PathVariable("eventId") Long eventId,
                                                               @Valid @RequestBody UpdateEventUserRequest newEventDto) {
        return eventService.updateEventByUserAndEventIds(userId, eventId, newEventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventStatus(@PathVariable("userId") Long userId,
                                                            @PathVariable("eventId") Long eventId,
                                                            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.updateEventStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
