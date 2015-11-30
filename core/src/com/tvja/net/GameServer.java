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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class GameServer {
    private static final int READBUFFER_LEN = 256;
    private ByteBuffer readBuffer;
    private DatagramSocket socket;
    private ByteArrayOutputStream bos;
    private List<NetworkObject> objects;

    private Map<SocketAddress, Player> players;

    private ByteArrayInputStream bis;
    private Kryo kryo;
    private Output out;
    private Input in;

    private FPSController fpsController;
    
    private boolean changed = true;

    public void create() {
        readBuffer = ByteBuffer.allocate(READBUFFER_LEN);

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
    
    public void start() throws IOException {
    	Selector selector = Selector.open();
    	DatagramChannel channel = DatagramChannel.open();
    	InetSocketAddress isa = new InetSocketAddress("0.0.0.0", Protocol.SERVER_PORT);
    	channel.socket().bind(isa);
    	channel.configureBlocking(false);
    	channel.register(selector, SelectionKey.OP_READ);
    	
    	while (true) {
    		selector.select();
    		Iterator selectedKeys = selector.selectedKeys().iterator();
    		while (selectedKeys.hasNext()) {
    			SelectionKey key = (SelectionKey) selectedKeys.next();
    			selectedKeys.remove();
    			
    			if (!key.isValid()) {
    				continue;
    			}
    			
    			if (key.isReadable()) {
    				boolean changed = read(selector, key);
    				if (changed) {
    					updateClients(selector);
    				}
    				key.interestOps(SelectionKey.OP_READ);
    			} else if (key.isWritable()) {
    				write(key);
    			}
    		}
    		
    		serverLogic();
    	}
    }

    public boolean read(Selector selector, SelectionKey key) throws IOException {
    	DatagramChannel chan = (DatagramChannel) key.channel();
        boolean received = false;	
        changed = false;

        readBuffer.clear();
        SocketAddress ad = chan.receive(readBuffer);
        if (ad != null) {
        	received = true;
        }
        
        InetSocketAddress fullAddress = (InetSocketAddress) ad;
        
        if (received) {
            bis = new ByteArrayInputStream(readBuffer.array());
            in = new Input(bis);
            ClientPacket cp = kryo.readObject(in, ClientPacket.class);
            switch (cp.getCode()) {
                case NEW_CLIENT:
                	changed = true;
                	addNewClient(selector, fullAddress);
                    break;
                case INPUT:
                	changed = true;
                	moveClient(ad, cp);
                    break;
				default:
					throw new RuntimeException("Invalid code.");
            }
        }
        
        return changed;
    }

    private void moveClient(SocketAddress ad, ClientPacket cp) {
        if (!players.containsKey(ad)) {
            log("Invalid client movement: " + ad);
            return;
        }
        Player player = players.get(ad);
        fpsController.updatePositionOrientation(player.getAvatar(), cp.getPlayerInput());
    }

    private void serverLogic() {

    }
    
    private void write(SelectionKey key) throws IOException {
    	DatagramChannel chan = (DatagramChannel) key.channel();
    	KeyData kd = (KeyData) key.attachment();
    //	System.out.println("write to: " + kd.address.toString());
    	chan.send(kd.info, kd.address);
    }

    private void addNewClient(Selector selector, InetSocketAddress ad) throws IOException {
        log("New client from: " + ad.toString());
        log("Client hash: " + ad.hashCode());

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
        sendServerPacket(selector, player, sp);
    }

    private void updateClients(Selector selector) throws IOException {
        ServerPacket sp = new ServerPacket(Protocol.Code.NET_OBJECTS, null, objects);

        for (Player player : players.values()) {
            sp.setClientAvatar(player.getAvatar().getID());
            sendServerPacket(selector, player, sp);
        }
    }

    private void sendServerPacket(Selector selector, Player player, ServerPacket sp) throws IOException {
    	DatagramChannel channel = DatagramChannel.open();
    	channel.configureBlocking(false);
    	SelectionKey clientKey = channel.register(selector, SelectionKey.OP_WRITE);
    	
        bos.reset();
        kryo.writeObject(out, sp);
        out.flush();
        byte[] data = bos.toByteArray();
        ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length);
        
        InetSocketAddress isa = player.getAddress();
        InetSocketAddress isaWithPort = new InetSocketAddress(isa.getHostName(), Protocol.CLIENT_PORT);
        KeyData kd = new KeyData();
        kd.address = isaWithPort;
        kd.info = buf;
        
        clientKey.attach(kd);
    }

    private void log(String s) {
        System.out.println(s);
    }
    
    private class KeyData {
    	public ByteBuffer info;
    	public InetSocketAddress address;
    }
}
