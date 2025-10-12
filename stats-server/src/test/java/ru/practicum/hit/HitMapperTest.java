package ru.practicum.hit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.HitDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HitMapperTest {

    @Autowired
    private HitMapper hitMapper;

    private Hit hit;
    private HitDto hitDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = LocalDateTime.of(2023, 4, 3, 2, 1, 0);

        hit = new Hit();
        hit.setId(1L);
        hit.setApp("app");
        hit.setUri("/event/1");
        hit.setIp("192.168.1.1");
        hit.setTimestamp(timestamp);

        hitDto = new HitDto(1L, "app", "/event/1", "192.168.1.1", timestamp.format(formatter));
    }

    @Test
    void fromHitToDto() {
        HitDto hitDtoCreated = hitMapper.fromHitToDto(hit);

        assertNotNull(hitDtoCreated);
        assertEquals(hit.getId(), hitDtoCreated.getId());
        assertEquals(hit.getApp(), hitDtoCreated.getApp());
        assertEquals(hit.getUri(), hitDtoCreated.getUri());
        assertEquals(hit.getIp(), hitDtoCreated.getIp());
        assertEquals(hit.getTimestamp().format(formatter), hitDtoCreated.getTimestamp());
    }

    @Test
    void fromHitToDtoInvalidHit() {
        assertThrows(
                NullPointerException.class,
                () -> hitMapper.fromDtoToHit(null),
                "HitDto cannot be null"
        );
    }

    @Test
    void fromDtoToHit() {
        Hit result = hitMapper.fromDtoToHit(hitDto);

        assertNotNull(result);
        assertEquals(hitDto.getId(), result.getId());
        assertEquals(hitDto.getApp(), result.getApp());
        assertEquals(hitDto.getUri(), result.getUri());
        assertEquals(hitDto.getIp(), result.getIp());
        assertEquals(LocalDateTime.parse(hitDto.getTimestamp(), formatter), result.getTimestamp());
    }

    @Test
    void fromDtoToHitInvalidDate() {
        HitDto invalidDto = new HitDto(1L, "app", "/event/1", "192.168.1.1", "13.01.");

        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            hitMapper.fromDtoToHit(invalidDto);
        });
    }
}