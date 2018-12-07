package com.ptoceti.osgi.ebus.impl.connection;

import com.ptoceti.osgi.ebus.impl.message.EbusResponse;

public class  EbusResponseListener {

    Object waitResponseLock = new Object();
    EbusResponse response =  null;
    boolean hasResponse = false;
    boolean messageFailed = false;

    public EbusResponseListener(){

    }

    public EbusResponse getResponse() throws  InterruptedException {
        while (hasResponse == false) {
            synchronized (waitResponseLock) {
                wait();
            }
        }
        return response;
    }

    public void setResponse(EbusResponse message){
        synchronized (waitResponseLock){
            response = message;
            hasResponse = true;
        }
    }

    public void setMessageFailed(){
        synchronized (waitResponseLock){
            response = null;
            hasResponse = false;
            messageFailed = true;
        }
    }
}
