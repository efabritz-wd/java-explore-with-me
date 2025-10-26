package ru.practicum.errors;

public class CommonForbiddenException extends RuntimeException {
    public CommonForbiddenException(String message) {
        super(message);
    }
}
