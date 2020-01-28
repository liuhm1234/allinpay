package com.pack.net;

public class NetAbortException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NetAbortException(String massage) {
        super(massage);
    }
}
