package ru.practicum.errors;

public class CommonConflictException extends RuntimeException {
    public CommonConflictException(String message) {
        super(message);
    }
}
