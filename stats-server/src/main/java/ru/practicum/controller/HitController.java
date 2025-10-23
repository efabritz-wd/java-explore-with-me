package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.HitService;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@RestController
public class HitController {
    private final HitService hitService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public HitDto createHit(@Valid @RequestBody HitDto hitDto) {
        log.info("HitDto received: " + hitDto);
        return hitService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(required = false) String uris,
                                   @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Fetching stats from {} to {}, uris: {}, unique: {}", start, end, uris, unique);

        List<String> urisList = (uris != null) ? Stream.of(uris.split(","))
                .map(uri -> UriUtils.decode(uri, StandardCharsets.UTF_8))
                .toList() : Collections.emptyList();

        log.info("Decoded URIs: {}", urisList);
        return hitService.getStats(start, end, urisList, unique);
    }
}

