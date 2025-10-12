package ru.practicum.service;


import ru.practicum.HitDto;
import ru.practicum.projection.StatsProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto createHit(HitDto hitDto);

    List<StatsProjection> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
