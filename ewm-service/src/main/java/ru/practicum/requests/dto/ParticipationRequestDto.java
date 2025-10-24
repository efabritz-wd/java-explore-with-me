package ru.practicum.requests.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.requests.RequestStatus;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;

    @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN)
    private LocalDateTime created;
    private Long requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
