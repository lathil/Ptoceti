package com.ptoceti.osgi.ebus.impl.exception;

public class BadCrcException extends Exception{

    public BadCrcException(){}

    public BadCrcException(String message){
        super(message);
    }
}
