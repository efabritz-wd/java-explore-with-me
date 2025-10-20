package ru.practicum.events;

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
    public List<EventFullDto> getAllFilteredEvents(@RequestParam List<Long> users,
                                                   @RequestParam List<String> states,
                                                   @RequestParam List<Long> categories,
                                                   @RequestParam LocalDateTime rangeStart,
                                                   @RequestParam LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllFilteredEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateFilteredEvent(@PathVariable Long eventId,
                                            @RequestBody UpdateEventAdminRequest eventUpdateDto) {
        return eventService.updateFilteredEvent(eventId, eventUpdateDto);
    }
}
