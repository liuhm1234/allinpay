package com.pack.net;

public class NetSendException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NetSendException(String massage){
        super(massage);
    }
}