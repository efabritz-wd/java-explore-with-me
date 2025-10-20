package ru.practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllPublicFilteredEvents(@RequestParam(required = false) String text,
                                                          @RequestParam(required = false) List<Long> categories,
                                                          @RequestParam(required = false) Boolean paid,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                          @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                          @RequestParam(required = false) EventSort sort,
                                                          @RequestParam(defaultValue = "0", required = false) Integer from,
                                                          @RequestParam(defaultValue = "10", required = false) Integer size,
                                                          HttpServletRequest request) {
        return eventService.getAllPublicFilteredEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{userId}")
    public EventFullDto getAllPublicFilteredEvents(@PathVariable Long userId, HttpServletRequest request) {
        return eventService.getPublicFilteredEventById(userId, request);
    }
}
