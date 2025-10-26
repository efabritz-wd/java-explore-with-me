package ru.practicum.errors;

public class CommonBadRequestException extends RuntimeException {
    public CommonBadRequestException(String message) {
        super(message);
    }
}
