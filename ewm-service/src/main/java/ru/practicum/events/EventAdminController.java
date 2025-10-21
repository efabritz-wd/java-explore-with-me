package ru.practicum.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.requests.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllFilteredEvents(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false)  List<String> states,
                                                   @RequestParam(required = false)  List<Long> categories,
                                                   @RequestParam(required = false)  LocalDateTime rangeStart,
                                                   @RequestParam(required = false)  LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getAllFilteredEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateFilteredEvent(@PathVariable Long eventId,
                                            @Valid @RequestBody UpdateEventAdminRequest eventUpdateDto) {
        return eventService.updateFilteredEvent(eventId, eventUpdateDto);
    }
}
