package ru.practicum.errors;

public class CommonNotFoundException extends RuntimeException {
    public CommonNotFoundException(String message) {
        super(message);
    }
}

