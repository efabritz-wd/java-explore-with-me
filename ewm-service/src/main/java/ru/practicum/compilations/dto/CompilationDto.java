package ru.practicum.compilations.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.dto.EventShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompilationDto {
    @NotBlank
    private Long id;

    private List<EventShortDto> events;

    @NotBlank
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;
}
