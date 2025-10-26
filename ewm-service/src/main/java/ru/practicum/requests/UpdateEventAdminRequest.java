package ru.practicum.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.locations.Location;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Nullable
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    @Nullable
    private Long category;

    @Nullable
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    @Nullable
    @JsonFormat(pattern = UtilPatterns.DATE_PATTERN)
    private LocalDateTime eventDate;

    @Nullable
    private Location location;

    @Nullable
    private Boolean paid;

    @Nullable
    private Integer participantLimit;

    @Nullable
    private Boolean requestModeration;

    @Nullable
    @Enumerated(EnumType.STRING)
    private StateAction stateAction;

    @Nullable
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;
}
