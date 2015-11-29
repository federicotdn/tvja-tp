package com.tvja.net;

import java.util.List;

/**
 * Created by Hobbit on 11/29/15.
 */
public class ServerPacket {
    private Protocol.Code code;
    private Integer clientId;
    private List<NetworkObject> networkObjects;
    private Integer clientAvatar;

    public ServerPacket(Protocol.Code code, Integer clientId, List<NetworkObject> networkObjects) {
        this.code = code;
        this.clientId = clientId;
        this.networkObjects = networkObjects;
    }

    public Integer getClientAvatar() {
		return clientAvatar;
	}

	public void setClientAvatar(Integer clientAvatar) {
		this.clientAvatar = clientAvatar;
	}

	public ServerPacket() {

    }

    public ServerPacket(Protocol.Code code, Integer id) {

        this.code = code;
        this.clientId = id;
    }

    public Protocol.Code getCode() {
        return code;
    }

    public Integer getId() {
        return clientId;
    }

    public List<NetworkObject> getNetworkObjects() {
        return networkObjects;
    }
}
