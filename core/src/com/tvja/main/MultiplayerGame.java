package com.tvja.main;

import com.badlogic.gdx.math.Vector3;

public class MultiplayerGame extends TPGameBase {

	private Vector3 ambientLight;
	
	@Override
	protected void init() {
		ambientLight = new Vector3(0.05f, 0.05f, 0.05f);
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
