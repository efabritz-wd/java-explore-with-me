package ru.practicum.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.requests.UpdateEventAdminRequest;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class EventAdminController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getAllFilteredEvents(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false)  List<String> states,
                                                   @RequestParam(required = false)  List<Long> categories,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN)  LocalDateTime rangeStart,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN)  LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getAllFilteredEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateFilteredEvent(@PathVariable Long eventId,
                                            @Valid @RequestBody UpdateEventAdminRequest eventUpdateDto) {
        return eventService.updateFilteredEvent(eventId, eventUpdateDto);
    }
}
