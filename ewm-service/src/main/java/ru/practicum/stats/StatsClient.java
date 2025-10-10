package ru.practicum.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.client.BaseClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (uris == null || uris.isEmpty()) {
            throw new IllegalArgumentException("URIs list cannot be empty");
        }

        Map<String, Object> parameters = Map.of(
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER),
               // "uris", Arrays.asList(uris.split(",")),
                "uris", uris,
                "unique", unique
        );

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}