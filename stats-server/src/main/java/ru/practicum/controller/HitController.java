package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.practicum.HitDto;
import ru.practicum.projection.StatsProjection;
import ru.practicum.service.HitService;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public HitDto createHit(@Valid @RequestBody HitDto hitDto) {
        log.info("HitDto received: " + hitDto);
        return hitService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsProjection> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam String uris,
                                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Fetching stats from {} to {}, uris: {}, unique: {}", start, end, uris, unique);


        String uriDecoded = UriUtils.decode(uris, StandardCharsets.UTF_8);
        log.info("Fetching stats from {} to {}, uris: {}, unique: {}", start, end, uriDecoded, unique);

        List<String> urisList = Arrays.asList(uriDecoded.split(","));
        log.info("Decoded URIs: {}", urisList);
        return hitService.getStats(start, end, urisList, unique);
    }
}

