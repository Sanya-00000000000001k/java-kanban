package com.yandex.app.exceptions;

public class CollisionException extends RuntimeException {

    public CollisionException() {
        super();
    }

    public CollisionException(String message) {
        super(message);
    }
}