package com.tvja.net;

import java.util.List;

/**
 * Created by Hobbit on 11/29/15.
 */
public class ServerPacket {
    private Protocol.Code code;
    private Integer id;
    private List<NetworkObject> networkObjects;

    public ServerPacket(Protocol.Code code, Integer id, List<NetworkObject> networkObjects) {
        this.code = code;
        this.id = id;
        this.networkObjects = networkObjects;
    }

    public ServerPacket() {

    }

    public ServerPacket(Protocol.Code code, Integer id) {

        this.code = code;
        this.id = id;
    }

    public Protocol.Code getCode() {
        return code;
    }

    public Integer getId() {
        return id;
    }

    public List<NetworkObject> getNetworkObjects() {
        return networkObjects;
    }
}
