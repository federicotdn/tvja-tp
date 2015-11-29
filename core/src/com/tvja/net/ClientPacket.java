package com.tvja.net;

/**
 * Created by Hobbit on 11/29/15.
 */
public class ClientPacket  {
    private Protocol.Code code;

    public ClientPacket() {

    }

    public Protocol.Code getCode() {
        return code;
    }

    public ClientPacket(Protocol.Code code) {

        this.code = code;
    }
}
