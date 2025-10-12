package ru.practicum.hit;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.HitDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Hit;
import ru.practicum.projection.StatsProjection;
import ru.practicum.repository.HitRepository;
import ru.practicum.service.HitService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ru.practicum.StatsServerApp.class)
public class HitServiceImplTest {

    @Autowired
    private HitService hitService;

    @Autowired
    private HitRepository hitRepository;

    private HitDto hitDto;

    private LocalDateTime timestamp;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        hitRepository.deleteAll();
        timestamp = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        hitDto = new HitDto(null, "app1", "/event/1", "192.168.1.1", timestamp.format(formatter));

        Hit hit1 = new Hit(null, "/event/1", "app1", "192.168.1.1", timestamp);
        Hit hit2 = new Hit(null, "/event/1", "app1", "192.168.1.1", timestamp.plusHours(1));
        Hit hit3 = new Hit(null, "/event/1", "app1", "192.168.1.2", timestamp.plusHours(2));
        Hit hit4 = new Hit(null, "/event/2", "app2", "192.168.1.3", timestamp.plusHours(3));

        hitRepository.saveAll(List.of(hit1, hit2, hit3, hit4));
    }

    @Test
    void createHit() {
        HitDto result = hitService.createHit(hitDto);

        assertNotNull(result);
        assertEquals(hitDto.getApp(), result.getApp());
        assertEquals(hitDto.getUri(), result.getUri());
        assertEquals(hitDto.getIp(), result.getIp());
        assertEquals(hitDto.getTimestamp(), result.getTimestamp());

        List<Hit> hits = hitRepository.findAll();
        assertEquals(5, hits.size());

        Hit savedHit = hits.stream()
                .filter(h -> h.getIp().equals("192.168.1.1") && h.getTimestamp().equals(timestamp))
                .findFirst()
                .orElse(null);
        assertNotNull(savedHit);
    }

    @Test
    void createinvalidHit() {
        HitDto invalidDto = new HitDto(null, null, null, null, null);

        assertThrows(
                NullPointerException.class,
                () -> hitService.createHit(invalidDto),
                "HitDto is invalid during hit creation"
        );
    }

    @Test
    void getStats() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
        List<String> uris = List.of("/event/1", "/event/2");

        List<StatsProjection> result = hitService.getStats(start, end, uris, true);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void countStats() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
        List<String> uris = List.of("/event/1", "/event/2");


        List<StatsProjection> result = hitService.getStats(start, end, uris, false);

        assertNotNull(result);
        assertEquals(2, result.size());

        StatsProjection stats1 = result.stream()
                .filter(s -> s.getUri().equals("/event/1") && s.getApp().equals("app1"))
                .findFirst()
                .orElse(null);
        assertNotNull(stats1, "Stats for /event/1 and app1 should exist");
        assertEquals("app1", stats1.getApp());
        assertEquals("/event/1", stats1.getUri());
        assertEquals(3L, stats1.getHits(), "Expected 3 hits for /event/1");
    }

    @Test
    void getStatsInvalidTimeRange() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        List<String> uris = List.of("/event/1");


        assertThrows(
                ValidationException.class,
                () -> hitService.getStats(start, end, uris, true),
                "Invalid time range"
        );
    }
}