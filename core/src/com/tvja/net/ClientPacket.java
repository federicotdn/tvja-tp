package com.tvja.net;

import com.tvja.camera.PlayerInput;

/**
 * Created by Hobbit on 11/29/15.
 */
public class ClientPacket {
    private Protocol.Code code;
    private PlayerInput playerInput;

    public ClientPacket() {

    }

    public Protocol.Code getCode() {
        return code;
    }

    public ClientPacket(Protocol.Code code) {

        this.code = code;
    }

    public ClientPacket(Protocol.Code code, PlayerInput playerInput) {
        this.code = code;
        this.playerInput = playerInput;
    }

    public PlayerInput getPlayerInput() {

        return playerInput;
    }
}


