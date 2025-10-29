package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 700)
    private String content;

    @JsonFormat
    @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN)
    private LocalDateTime created;

    private String userName;

    private Long eventId;
}
