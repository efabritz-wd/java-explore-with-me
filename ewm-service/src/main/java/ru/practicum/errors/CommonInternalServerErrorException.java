package ru.practicum.errors;

public class CommonInternalServerErrorException extends RuntimeException {
    public CommonInternalServerErrorException(String message) {
        super(message);
    }
}
