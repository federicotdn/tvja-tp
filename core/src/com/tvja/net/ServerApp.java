package com.tvja.net;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApp extends ApplicationAdapter {
    private static final int READBUFFER_LEN = 256;
    DatagramSocket socket;
    byte[] readBuffer;
    ByteArrayOutputStream bos;
    ObjectOutput objectOut;
    private ByteArrayInputStream bis;
    private ObjectInput objectIn;
    List<NetworkObject> objects;
    Set<Player> players;
    private Stage stage;
    private Table table;
    private TextArea console;

    @Override
    public void create() {
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        console = new TextArea("", skin);
        console.setFillParent(true);
        console.setDisabled(true);
        table.addActor(console);

        try {
            socket = new DatagramSocket(new InetSocketAddress("0.0.0.0", Protocol.SERVER_PORT));
            socket.setSoTimeout(16);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to open socket.");
        }

        readBuffer = new byte[READBUFFER_LEN];

        bos = new ByteArrayOutputStream();

        try {
            objectOut = new ObjectOutputStream(bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        objects = new ArrayList<NetworkObject>();
        players = new HashSet<Player>();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        checkExit();
        update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void update() {
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
            bis = new ByteArrayInputStream(Arrays.copyOfRange(readBuffer, 1, readBuffer.length));
            Protocol.Code c;
            try {
                objectIn = new ObjectInputStream(bis);
                c = Protocol.parseHeader(objectIn.readByte());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            switch (c) {
                case NEW_CLIENT:
                    addNewClient(packet);
                    break;
            }
        }
    }

    private void addNewClient(DatagramPacket packet) {
        InetAddress ad = packet.getAddress();
        log("New client from: " + ad.toString());

        Player player = new Player(ad);
        if (!players.contains(player)) {
            log("Player added to set.");
            players.add(player);
            NetworkObject obj = new NetworkObject();
            player.setAvatar(obj);
        }

        bos.reset();
        try {
            objectOut.writeByte(Protocol.Code.ACK_CLIENT.getHeader());
            objectOut.writeInt(player.getId());
            objectOut.flush();
            byte[] data = bos.toByteArray();
            DatagramPacket outPacket = new DatagramPacket(data, data.length, player.getAddress(), Protocol.CLIENT_PORT);
            socket.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateClients() {
        for (NetworkObject obj : objects) {
            for (Player player : players) {
                bos.reset();

                try {
                    objectOut.writeObject(obj);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                byte[] data = bos.toByteArray();
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void log(String s) {
        console.appendText(s + "\n");
    }

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
