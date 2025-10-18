package com.omega.casino.exception;

public class UnderagePlayerException extends RuntimeException {
    public UnderagePlayerException(String message) {
        super(message);
    }
}
