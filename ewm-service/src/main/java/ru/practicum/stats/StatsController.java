package ru.practicum.stats;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;

import java.time.LocalDateTime;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "")
@RequiredArgsConstructor
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> createHit(@Valid @RequestBody HitDto hitDto) {
        return statsClient.addHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) String uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {

        log.info("Sendind start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }
}

