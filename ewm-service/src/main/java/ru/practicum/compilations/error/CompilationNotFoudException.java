package ru.practicum.compilations.error;

public class CompilationNotFoudException extends RuntimeException {
    public CompilationNotFoudException(String message, String causeValue) {
        super(message);
    }
}
