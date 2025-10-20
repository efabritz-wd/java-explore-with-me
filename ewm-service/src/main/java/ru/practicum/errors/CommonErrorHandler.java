package ru.practicum.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;


@RestControllerAdvice
public class CommonErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError commonBadRequest(final CommonBadRequestException e) {
        return new ApiError(
                e.getMessage(),
                "Incorrectly made request.",
                HttpErrorStatus.BAD_REQUEST.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError commonConflict(final CommonConflictException e) {
        return new ApiError(
                e.getMessage(),
                "Integrity constraint has been violated.",
                HttpErrorStatus.CONFLICT.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError commonNotFound(final CommonNotFoundException e) {
        return new ApiError(
                e.getMessage(),
                "The required object was not found.",
                HttpErrorStatus.NOT_FOUND.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError commonForbidden(final CommonForbiddenException e) {
        return new ApiError(
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpErrorStatus.FORBIDDEN.getName(),
                LocalDateTime.now()
        );
    }
}