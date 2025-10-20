package ru.practicum.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
//@RequiredArgsConstructor
public class ApiError {
    private String message;

    private String reason;

    private HttpErrorStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private List<String> errors = new ArrayList<>();

    public ApiError(String message, String reason, String status, LocalDateTime timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = HttpErrorStatus.valueOf(status);
        this.timestamp = timestamp;
    }
}
