package com.example.zasobnik.flatbox.exceptions;

public class ZipCreationRuntimeException extends RuntimeException {
    public ZipCreationRuntimeException(String message) {
        super(message);
    }

    public ZipCreationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
