package ru.practicum.hit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.model.Hit;
import ru.practicum.projection.StatsProjection;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HitRepositoryTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private Hit hit1;
    private Hit hit2;
    private Hit hit3;
    private Hit hit4;
    private Hit hit5;


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HitRepository hitRepository;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2023, 4, 3, 2, 1);
        end = LocalDateTime.of(2023, 5, 4, 3, 2);
        uris = List.of("/event/1", "/event/2");

        hit1 = new Hit(null, "/event/1", "app1", "192.168.1.1", start.plusHours(1));
        hit2 = new Hit(null, "/event/1", "app1", "192.168.1.1", start.plusHours(2));
        hit3 = new Hit(null, "/event/1", "app1", "192.168.1.2", start.plusHours(3));
        hit4 = new Hit(null, "/event/2", "app2", "192.168.1.3", start.plusHours(4));
        hit5 = new Hit(null, "/event/2", "app2", "192.168.1.4", start.plusHours(5));

        entityManager.persist(hit1);
        entityManager.persist(hit2);
        entityManager.persist(hit3);
        entityManager.persist(hit4);
        entityManager.persist(hit5);
        entityManager.flush();

        List<Hit> allHits = entityManager.getEntityManager()
                .createQuery("SELECT h FROM Hit h", Hit.class)
                .getResultList();
        assertEquals(5, allHits.size(), "Expected 5 hits to be persisted in the database");
        System.out.println("2");
    }

    @Test
    void findHitsByTimeRangeAndUris() {

        List<StatsProjection> result = hitRepository.findHitsByTimeRangeAndUris(start, end, uris, true);

        assertNotNull(result);
        assertEquals(2, result.size());

        StatsProjection stats1 = result.stream()
                .filter(s -> s.getUri().equals("/event/1") && s.getApp().equals("app1"))
                .findFirst()
                .orElse(null);
        assertNotNull(stats1);
        assertEquals("app1", stats1.getApp());
        assertEquals("/event/1", stats1.getUri());
        assertEquals(2L, stats1.getHits());

        StatsProjection stats2 = result.stream()
                .filter(statsProjection -> statsProjection.getUri().equals("/event/2") && statsProjection.getApp().equals("app2"))
                .findFirst()
                .orElse(null);
        assertNotNull(stats2);
        assertEquals("app2", stats2.getApp());
        assertEquals("/event/2", stats2.getUri());
        assertEquals(2L, stats2.getHits());
    }

    @Test
    void findHitsEmptyResult() {
        List<String> uris = List.of("/event/3");
        List<StatsProjection> result = hitRepository.findHitsByTimeRangeAndUris(start, end, uris, true);

        assertTrue(result.isEmpty());
    }

    @Test
    void findHitsInvalidPeriod() {
        List<StatsProjection> result = hitRepository.findHitsByTimeRangeAndUris(start, end.minusYears(3), uris, true);
        assertTrue(result.isEmpty());
    }

    @Test
    void findHitsNullUris() {
        List<StatsProjection> result = hitRepository.findHitsByTimeRangeAndUris(start, end, null, true);
        assertTrue(result.isEmpty());
    }
}