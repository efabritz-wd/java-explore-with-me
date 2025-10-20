package ru.practicum.compilations.error;

public class CompilationConflictException extends RuntimeException {
    public CompilationConflictException(String message) {
        super(message);
    }
}
