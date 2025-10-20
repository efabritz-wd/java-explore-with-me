package ru.practicum.compilations.error;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.errors.ApiError;
import ru.practicum.errors.HttpErrorStatus;

import java.time.LocalDateTime;

/*
*
    private String message;

    private String reason;

    private HttpErrorStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
*
* */

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError compilationBadRequest(final CompilationBadRequestException e) {
        return new ApiError(
            e.getMessage(),
                "Incorrectly made request.",
                HttpErrorStatus.BAD_REQUEST.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError compilationPostBadRequest(final CompilationPostException e) {
        return new ApiError(
                e.getMessage(),
                "Incorrectly made request.",
                HttpErrorStatus.BAD_REQUEST.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError compilationConflict(final CompilationConflictException e) {
        return new ApiError(
                e.getMessage(),
                "Integrity constraint has been violated.",
                HttpErrorStatus.CONFLICT.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError compilationNotFound(final CompilationNotFoudException e) {
        return new ApiError(
                e.getMessage(),
                "The required object was not found.",
                HttpErrorStatus.NOT_FOUND.getName(),
                LocalDateTime.now()
        );
    }
}