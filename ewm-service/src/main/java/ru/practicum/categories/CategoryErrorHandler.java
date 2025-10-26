package ru.practicum.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.errors.ApiError;
import ru.practicum.errors.CategoryConflictException;
import ru.practicum.errors.CategoryNotFoundException;

import java.time.LocalDateTime;

import static ru.practicum.errors.HttpErrorStatus.CONFLICT;
import static ru.practicum.errors.HttpErrorStatus.NOT_FOUND;

@RestControllerAdvice
public class CategoryErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotExistException(final CategoryNotFoundException e) {
        return new ApiError(
                e.getMessage(),
                "Category does not exist",
                NOT_FOUND.getName(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryNotEmptyException(final CategoryConflictException e) {
        return new ApiError(
                e.getMessage(),
                "Conditions are wrong",
                CONFLICT.getName(),
                LocalDateTime.now()
        );
    }

}