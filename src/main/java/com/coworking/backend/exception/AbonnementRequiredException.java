package com.coworking.backend.exception;

public class AbonnementRequiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AbonnementRequiredException(String message) {
        super(message);
    }
}