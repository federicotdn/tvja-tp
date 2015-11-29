package com.tvja.net;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tvja.render.ModelInstance;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ServerApp extends ApplicationAdapter {
    private static final int READBUFFER_LEN = 256;
    private DatagramSocket socket;
    byte[] readBuffer;
    private ByteArrayOutputStream bos;
    private List<NetworkObject> objects;
    
    private Set<Player> players;
    private List<Player> playerList;
    
    private ByteArrayInputStream bis;
    private Stage stage;
    private Table table;
    private TextArea console;
    private Kryo kryo;
    private Output out;
    private Input in;

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

        objects = new ArrayList<NetworkObject>();
        players = new HashSet<Player>();
        playerList = new ArrayList<Player>();

        kryo = new Kryo();
        kryo.register(ServerPacket.class);
        kryo.register(ClientPacket.class);
        kryo.register(LinkedList.class);
        kryo.register(ArrayList.class);

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
            bis = new ByteArrayInputStream(readBuffer);
            in = new Input(bis);
            ClientPacket cp = kryo.readObject(in, ClientPacket.class);
            switch (cp.getCode()) {
                case NEW_CLIENT:
                    addNewClient(packet);
                    break;
            }
        }
        
        serverLogic();
        updateClients();
    }
    
    public void serverLogic() {
    	
    }

    private void addNewClient(DatagramPacket packet) {
        InetAddress ad = packet.getAddress();
        log("New client from: " + ad.toString());

        Player player = new Player(ad);
        if (!players.contains(player)) {
            log("Player added to set.");
            players.add(player);
            playerList.add(player);
            NetworkObject obj = new NetworkObject();
            obj.genID();
            player.setAvatar(obj);
        }

        
        ServerPacket sp = new ServerPacket(Protocol.Code.ACK_CLIENT, player.getId());
        sendServerPacket(player, sp);
    }

	public void updateClients() {
		ServerPacket sp = new ServerPacket(Protocol.Code.NET_OBJECTS, null, objects);

		for (Player player : players) {
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
