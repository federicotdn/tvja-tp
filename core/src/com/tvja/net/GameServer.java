package com.tvja.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tvja.camera.FPSController;
import com.tvja.camera.PlayerInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int READBUFFER_LEN = 256;
    byte[] readBuffer;
    private DatagramSocket socket;
    private ByteArrayOutputStream bos;
    private List<NetworkObject> objects;

    private Map<InetAddress, Player> players;

    private ByteArrayInputStream bis;
    private Kryo kryo;
    private Output out;
    private Input in;

    private FPSController fpsController;
    
    private boolean changed = true;

    public void create() {
        try {
            socket = new DatagramSocket(new InetSocketAddress("0.0.0.0", Protocol.SERVER_PORT));
            socket.setSoTimeout(1);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to open socket.");
        }

        readBuffer = new byte[READBUFFER_LEN];

        bos = new ByteArrayOutputStream();

        objects = new ArrayList<NetworkObject>();
        players = new HashMap<>();

        kryo = new Kryo();
        kryo.register(ServerPacket.class);
        kryo.register(ClientPacket.class);
        kryo.register(LinkedList.class);
        kryo.register(ArrayList.class);
        kryo.register(PlayerInput.class);

        out = new Output(bos);

        //TEST SCENARIO
        NetworkObject no = new NetworkObject();
        no.genID();
        no.translate(2, 0.2f, 0);
        objects.add(no);

        no = new NetworkObject();
        no.genID();
        no.translate(4, 0.5f, 0);
        objects.add(no);

        no = new NetworkObject();
        no.genID();
        no.translate(6, 1f, 0);
        objects.add(no);

        fpsController = new FPSController();
    }

    public void update() {
        readBuffer[0] = 0;
        boolean received = false;
        changed = false;

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
            ClientPacket cp = kryo.readObject(in, ClientPacket.class);
            switch (cp.getCode()) {
                case NEW_CLIENT:
                	changed = true;
                    addNewClient(packet);
                    break;
                case INPUT:
                	changed = true;
                    moveClient(packet.getAddress(), cp);
                    break;
            }
        }

        serverLogic();
        
        if (changed) {
        	updateClients();        	
        }
    }

    private void moveClient(InetAddress address, ClientPacket cp) {
        if (!players.containsKey(address)) {
            log("Invalid client movement: " + address);
            return;
        }
        Player player = players.get(address);
        fpsController.updatePositionOrientation(player.getAvatar(), cp.getPlayerInput());
    }

    private void serverLogic() {

    }

    private void addNewClient(DatagramPacket packet) {
        InetAddress ad = packet.getAddress();
        log("New client from: " + ad.toString());

        Player player;
        if (!players.containsKey(ad)) {
            player = new Player(ad);
            log("Player added to set.");
            players.put(player.getAddress(), player);
            NetworkObject obj = new NetworkObject();
            obj.genID();
            player.setAvatar(obj);
            objects.add(obj);
        } else {
            player = players.get(ad);
        }

        ServerPacket sp = new ServerPacket(Protocol.Code.ACK_CLIENT, player.getId());
        sendServerPacket(player, sp);
    }

    private void updateClients() {
        ServerPacket sp = new ServerPacket(Protocol.Code.NET_OBJECTS, null, objects);

        for (Player player : players.values()) {
            sp.setClientAvatar(player.getAvatar().getID());
            sendServerPacket(player, sp);
        }
    }

    private void sendServerPacket(Player player, ServerPacket sp) {
        bos.reset();
        kryo.writeObject(out, sp);
        out.flush();
        byte[] data = bos.toByteArray();
        DatagramPacket outPacket = new DatagramPacket(data, data.length, player.getAddress(), Protocol.CLIENT_PORT);
        try {
            socket.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void log(String s) {
        System.out.println(s);
    }
}
