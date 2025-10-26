package ru.practicum.users.dto;

import jakarta.validation.constraints.Email;
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
public class UserDto {
    private Long id;
    @NotBlank
    @Size(min = 2, max = 250, message = "Name length must be between 2 and 250 characters")
    private String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "email length must be between 6 and 254 characters")
    private String email;
}
