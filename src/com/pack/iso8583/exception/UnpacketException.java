package com.pack.iso8583.exception;

public class UnpacketException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnpacketException() {
    }

    public UnpacketException(String message) {
        super(message);
    }
}