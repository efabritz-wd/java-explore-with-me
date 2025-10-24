package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.locations.Location;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    @NotNull
    @JsonFormat(pattern = UtilPatterns.DATE_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid = false;

    @Min(value = 0, message = "Participant limit must be 0 or greater")
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;
}
