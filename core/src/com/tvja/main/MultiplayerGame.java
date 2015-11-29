package com.tvja.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tvja.net.ClientPacket;
import com.tvja.net.Protocol;
import com.tvja.net.ServerPacket;

import java.io.*;
import java.net.*;

public class MultiplayerGame extends TPGameBase {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int READBUFFER_LEN = 256;
    private DatagramSocket socket;
    private ByteArrayOutputStream bos;
    private ByteArrayInputStream bis;
    private Vector3 ambientLight;
    private byte[] readBuffer;
    private Integer id;

    private Kryo kryo;
    private Output out;
    private Input in;

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

        bos.reset();
        kryo = new Kryo();
        kryo.register(ServerPacket.class);
        kryo.register(ClientPacket.class);

        out = new Output(bos);

        ClientPacket cp = new ClientPacket(Protocol.Code.NEW_CLIENT);
        kryo.writeObject(out, cp);
        out.flush();

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
                in = new Input(bis);
                ServerPacket sp = kryo.readObject(in, ServerPacket.class);
                if (sp.getCode() == Protocol.Code.ACK_CLIENT) {
                    id = sp.getId();
                    System.out.println("Connected with id: " + id);
                    break;
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
