package ru.practicum.hit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import jakarta.persistence.PersistenceException;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Hit hit;
    private LocalDateTime timestamp;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        timestamp = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        hit = new Hit();
        hit.setId(1L);
        hit.setApp("app1");
        hit.setUri("/event/1");
        hit.setIp("192.168.1.1");
        hit.setTimestamp(timestamp);
    }

    @Test
    void saveHitWithAllFields() {
        Hit hitToPersist = new Hit(null, "/event/1", "app1", "192.168.1.1", timestamp);

        Hit savedHit = entityManager.persistFlushFind(hitToPersist);


        assertNotNull(savedHit.getId(), "ID should be generated");
        assertEquals("/event/1", savedHit.getUri());
        assertEquals("app1", savedHit.getApp());
        assertEquals("192.168.1.1", savedHit.getIp());
        assertEquals(timestamp, savedHit.getTimestamp());
    }

    @Test
    void saveHitInvalidUri() {
        Hit invalidHit = new Hit(null, null, "app1", "192.168.1.1", timestamp);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistFlushFind(invalidHit),
                "Null uri during hit saving"
        );
    }

    @Test
    void saveHitInvalidApp() {
        Hit invalidHit = new Hit(null, "/event/1", null, "192.168.1.1", timestamp);

        assertThrows(
                PersistenceException.class,
                () -> entityManager.persistFlushFind(invalidHit),
                "Null app during hit saving"
        );
    }

    @Test
    void hitEqualityCheck() {
        Hit hit1 = new Hit(1L, "/event/1", "app1", "192.168.1.1", timestamp);
        Hit hit2 = new Hit(2L, "/event/1", "app1", "192.168.1.1", timestamp);

        Hit hit1changed = new Hit(1L, "/event/1", "app2", "192.168.1.1", timestamp);

        assertFalse(hit1.equals(hit2));
        assertTrue(hit1.equals(hit1changed));
    }

}
