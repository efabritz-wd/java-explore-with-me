package ru.practicum.hit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.controller.HitController;
import ru.practicum.projection.StatsProjection;
import ru.practicum.service.HitService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HitController.class)
class HitControllerTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @MockBean
    private HitService hitService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private HitDto hitDto;
    private StatsProjection statsProjection;

    @BeforeEach
    void setUp() {
        hitDto = new HitDto(null, "app", "/event/1", "192.168.1.1", LocalDateTime.now().format(formatter));

        statsProjection = new StatsProjection() {
            @Override
            public String getApp() {
                return "app";
            }

            @Override
            public String getUri() {
                return "/event/1";
            }

            @Override
            public Long getHits() {
                return 10L;
            }
        };
    }

    @Test
    void createHit() throws Exception {
        HitDto hitDto = new HitDto(1L, this.hitDto.getApp(), this.hitDto.getUri(), this.hitDto.getIp(), this.hitDto.getTimestamp());
        when(hitService.createHit(any(HitDto.class))).thenReturn(hitDto);

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.hitDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(hitDto.getId()))
                .andExpect(jsonPath("$.app").value(hitDto.getApp()))
                .andExpect(jsonPath("$.uri").value(hitDto.getUri()))
                .andExpect(jsonPath("$.ip").value(hitDto.getIp()))
                .andExpect(jsonPath("$.timestamp").value(hitDto.getTimestamp()));

        verify(hitService, times(1)).createHit(any(HitDto.class));
    }

    @Test
    void createHitWithNullParams() throws Exception {
        HitDto invalidDto = new HitDto(null, null, null, null, LocalDateTime.now().format(formatter));


        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().is5xxServerError());

        verify(hitService, never()).createHit(any(HitDto.class));
    }

    @Test
    void getStatsList() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 3, 2, 1);
        List<String> uris = List.of("/event/1", "/event/2");


        StatsDto statsDto = new StatsDto();
        statsDto.setApp(statsProjection.getApp());
        statsDto.setUri(statsProjection.getUri());
        statsDto.setHits(Math.toIntExact(statsProjection.getHits()));


        List<StatsDto> statsList = List.of(statsDto);
        when(hitService.getStats(eq(start), eq(end), eq(uris), eq(true))).thenReturn(statsList);


        mockMvc.perform(get("/stats")
                        .param("start", start.format(formatter))
                        .param("end", end.format(formatter))
                        .param("uris", "/event/1", "/event/2")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app"))
                .andExpect(jsonPath("$[0].uri").value("/event/1"))
                .andExpect(jsonPath("$[0].hits").value(10L));

        verify(hitService, times(1)).getStats(start, end, uris, true);
    }

    @Test
    void getStatsListWithInvalidParams() throws Exception {
        LocalDateTime end = LocalDateTime.of(2023, 3, 3, 3, 0);

        mockMvc.perform(get("/stats")
                        .param("end", end.format(formatter))
                        .param("uris", "/event/1"))
                .andExpect(status().is5xxServerError());

        verify(hitService, never()).getStats(any(), any(), any(), any());
    }
}