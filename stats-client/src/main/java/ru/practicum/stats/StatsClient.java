package ru.practicum.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.BaseClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        if (hitDto == null) {
            throw new IllegalArgumentException("HitDto cannot be null");
        }
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end,
                                           String uris, boolean unique) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }


        String urisString = (uris == null || uris.isEmpty()) ? "" : uris;

        Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("start", start.format(FORMATTER));
        parameters.put("end", end.format(FORMATTER));
        parameters.put("unique", unique);

        if (urisString.isEmpty()) {
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }

        parameters.put("uris", urisString);

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);

    }
}