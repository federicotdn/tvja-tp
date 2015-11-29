package com.tvja.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class ServerApp extends ApplicationAdapter {
	private static final int PORT = 3003;
	private static final int READBUFFER_LEN = 512;
	
	private Stage stage;
	private Table table;
	private TextArea console;
	DatagramSocket socket;
	byte[] readBuffer;
	
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
			socket = new DatagramSocket(new InetSocketAddress("0.0.0.0", PORT));
			socket.setSoTimeout(16);
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to open socket.");
		}
		
		readBuffer = new byte[READBUFFER_LEN];
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
		DatagramPacket packet = new DatagramPacket(readBuffer, READBUFFER_LEN);
		try {
			socket.receive(packet);
		} catch (SocketTimeoutException e) {
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
