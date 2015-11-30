package com.tvja.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tvja.net.GameServer;
import com.tvja.net.ServerApp;

public class ServerLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "TVJA Server";
		
		//new LwjglApplication(new ServerApp(), config);
		GameServer gs = new GameServer();
		System.out.println("start server");
		gs.create();
		while (true) {
			gs.update();
		}
	}
}