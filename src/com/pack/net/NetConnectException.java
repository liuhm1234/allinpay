package com.pack.net;

public class NetConnectException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NetConnectException(String massage){
        super(massage);
    }
}
