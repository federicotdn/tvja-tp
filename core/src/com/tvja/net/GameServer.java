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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class GameServer {
    private static final int READBUFFER_LEN = 256;
    private ByteBuffer readBuffer;
    private ByteArrayOutputStream bos;
    private List<NetworkObject> objects;

    private Map<SocketAddress, Player> players;

    private ByteArrayInputStream bis;
    private Kryo kryo;
    private Output out;
    private Input in;

    private FPSController fpsController;
    
    private Selector selector;
    private DatagramChannel outChannel;
    
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
    	selector = Selector.open();
    	outChannel = DatagramChannel.open();
    	InetSocketAddress isa = new InetSocketAddress("0.0.0.0", Protocol.SERVER_PORT);
    	outChannel.socket().bind(isa);
    	outChannel.configureBlocking(false);
    	outChannel.register(selector, SelectionKey.OP_READ);
    	
    	while (true) {
    		selector.select(2000);
    		Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
    		while (selectedKeys.hasNext()) {
    			SelectionKey key = (SelectionKey) selectedKeys.next();
    			selectedKeys.remove();
    			
    			if (!key.isValid()) {
    				continue;
    			}
    			
    			if (key.isReadable()) {
    				boolean changed = read(key);
    				if (changed) {
    					updateClients();
    				}
    				key.interestOps(SelectionKey.OP_READ);
    			} else if (key.isWritable()) {
    				write(key);
    			}
    		}
    		
    		serverLogic();
    	}
    }

    public boolean read(SelectionKey key) throws IOException {
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
                	addNewClient(fullAddress);
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
    	int written = chan.send(kd.info, kd.address);
    	if (written > 0 && written < 260)
    		System.out.println("written: " + Integer.valueOf(written).toString()); 
    	key.interestOps(0);
    }

    private void addNewClient(InetSocketAddress ad) throws IOException {
    	boolean newPlayer = false;
    	
        Player player;
        if (!players.containsKey(ad)) {
        	newPlayer = true;
            log("New client from: " + ad.toString());
            log("Client hash: " + ad.hashCode());
            player = new Player(ad);
            log("Player added to set.");
            players.put(player.getAddress(), player);
            NetworkObject obj = new NetworkObject();
            obj.genID();
            player.setAvatar(obj);
            objects.add(obj);
            
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_WRITE);
            player.setChannel(channel);
        } else {
            player = players.get(ad);
        }

        ServerPacket sp = new ServerPacket(Protocol.Code.ACK_CLIENT, player.getId());
       // System.out.println(player.getChannel().keyFor(selector).interestOps());
        if (player.getChannel().keyFor(selector).interestOps() == 0 || newPlayer) {
        	//System.out.println("send client packet");
        	sendServerPacket(player, sp);        	
        }
    }

    private void updateClients() throws IOException {
        ServerPacket sp = new ServerPacket(Protocol.Code.NET_OBJECTS, null, objects);

        for (Player player : players.values()) {
            sp.setClientAvatar(player.getAvatar().getID());
            if (player.getChannel().keyFor(selector).interestOps() == 0) {
            	sendServerPacket(player, sp);            	
            }
        }
    }

    private void sendServerPacket(Player player, ServerPacket sp) throws IOException {
    	SelectionKey clientKey = player.getChannel().keyFor(selector);

        bos.reset();
        kryo.writeObject(out, sp);
        out.flush();
        byte[] data = bos.toByteArray();
        ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length);
        buf.position(0);
        buf.mark();
        
        InetSocketAddress isa = player.getAddress();
        InetSocketAddress isaWithPort = new InetSocketAddress(isa.getHostName(), Protocol.CLIENT_PORT);
        KeyData kd = new KeyData();
        kd.address = isaWithPort;
        kd.info = buf;
        
        clientKey.attach(kd);
        clientKey.interestOps(SelectionKey.OP_WRITE);
    }

    private void log(String s) {
        System.out.println(s);
    }
    
    private class KeyData {
    	public ByteBuffer info;
    	public InetSocketAddress address;
    }
}
