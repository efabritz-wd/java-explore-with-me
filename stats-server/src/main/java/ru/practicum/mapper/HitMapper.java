package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.model.Hit;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HitMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UtilPatterns.DATE_PATTERN);

    public HitDto fromHitToDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().format(formatter)
        );
    }

    public Hit fromDtoToHit(HitDto hitDto) {
        LocalDateTime hitTimestamp = LocalDateTime.parse(hitDto.getTimestamp(), formatter);

        return new Hit(
                hitDto.getId(),
                hitDto.getUri(),
                hitDto.getApp(),
                hitDto.getIp(),
                hitTimestamp
        );
    }

}
