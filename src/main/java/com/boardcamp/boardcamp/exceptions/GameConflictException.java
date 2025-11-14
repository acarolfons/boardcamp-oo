package com.boardcamp.boardcamp.exceptions;

public class GameConflictException extends RuntimeException {
    public GameConflictException(String message) {
        super(message);
    }
}