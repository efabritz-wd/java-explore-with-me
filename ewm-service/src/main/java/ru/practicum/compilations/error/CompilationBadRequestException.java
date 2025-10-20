package ru.practicum.compilations.error;

public class CompilationBadRequestException extends RuntimeException {
    public CompilationBadRequestException(String message, String causeValue) {
        super(message);
    }
}
