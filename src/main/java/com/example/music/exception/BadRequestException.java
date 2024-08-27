package com.example.music.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, Long id) {
        super(String.format(message, id));
    }
}
