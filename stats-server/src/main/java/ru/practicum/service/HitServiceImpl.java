package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.projection.StatsProjection;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    public HitDto createHit(HitDto hitDto) {
        Hit hitAfterMapper = hitMapper.fromDtoToHit(hitDto);

        Hit hit = hitRepository.save(hitAfterMapper);
        log.info("Hit created: ", hit);

        HitDto hitDtoAfterSave = hitMapper.fromHitToDto(hit);
        return hitDtoAfterSave;
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Start date should be before end date");
        }

        List<StatsProjection> statsList = uris.isEmpty() ? hitRepository.findHitsByTimeRange(start, end, unique) :
                hitRepository.findHitsByTimeRangeAndUris(start, end, uris, unique);

        List<StatsDto> statsDtos = statsList.stream()
                .map(stats -> {
                    StatsDto statsDto = new StatsDto();
                    statsDto.setApp(stats.getApp());
                    statsDto.setUri(stats.getUri());
                    statsDto.setHits(Math.toIntExact(stats.getHits()));
                    return statsDto;
                })
                .collect(Collectors.toList());

        return statsDtos;
    }
}
