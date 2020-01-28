package com.pack.net;

public class NetReceiveException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NetReceiveException(String massage){
        super(massage);
    }
}
