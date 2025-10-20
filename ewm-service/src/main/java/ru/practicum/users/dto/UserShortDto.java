package ru.practicum.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserShortDto {
    @NotBlank
    private Long id;

    @NotBlank
    @Size(min = 2, max = 250, message = "Name length must be between 2 and 250 characters")
    private String name;
}
