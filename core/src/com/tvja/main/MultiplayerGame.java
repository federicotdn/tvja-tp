package com.tvja.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.tvja.net.Protocol;

import java.io.*;
import java.net.*;

public class MultiplayerGame extends TPGameBase {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int READBUFFER_LEN = 256;
    private DatagramSocket socket;
    private ByteArrayOutputStream bos;
    private ObjectOutput objectOut;
    private ByteArrayInputStream bis;
    private ObjectInput objectIn;
    private Vector3 ambientLight;
    private byte[] readBuffer;
    private Integer id;

    @Override
    protected void init() {
        ambientLight = new Vector3(0.05f, 0.05f, 0.05f);

        Gdx.input.setCursorCatched(false);

        try {
            socket = new DatagramSocket(new InetSocketAddress(SERVER_ADDRESS, Protocol.CLIENT_PORT));
            socket.setSoTimeout(16);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        bos = new ByteArrayOutputStream();
        try {
            objectOut = new ObjectOutputStream(bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bos.reset();
        try {
            objectOut.writeByte(Protocol.Code.NEW_CLIENT.getHeader());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = bos.toByteArray();
        readBuffer = new byte[READBUFFER_LEN];
        while (true) {
            DatagramPacket outPakcet = new DatagramPacket(data, data.length,
                    new InetSocketAddress(SERVER_ADDRESS, Protocol.SERVER_PORT));
            try {
                socket.send(outPakcet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            readBuffer[0] = 0;
            boolean received = false;

            DatagramPacket packet = new DatagramPacket(readBuffer, READBUFFER_LEN);
            try {
                socket.receive(packet);
                received = true;
            } catch (SocketTimeoutException e) {
            /* DO NOTHING */
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (received) {
                bis = new ByteArrayInputStream(readBuffer);
                try {
                    objectIn = new ObjectInputStream(bis);
                    Protocol.Code c = Protocol.parseHeader(objectIn.readByte());
                    if (c == Protocol.Code.ACK_CLIENT) {
                        id = objectIn.readInt();
                        System.out.println("Connected with id: " + id);
                        break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void update() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Vector3 getAmbientLight() {
        return ambientLight;
    }

}
