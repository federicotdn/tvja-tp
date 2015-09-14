package com.tvja.main;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.utils.AssetUtils;

public class TestGame extends TPGameBase {

	private Texture img, img2;
	private Mesh shipMesh;
	private Mesh cubeMesh;

	private float angle = 0;
	
	private Vector3 ambientLight;
	
	@Override
	protected void init() {
		// Initialize assets
		img = AssetUtils.textureFromFile("models/ship.png");
		img2 = AssetUtils.textureFromFile("models/plain.jpg");

		shipMesh = AssetUtils.meshFromFile("models/ship.obj");
		cubeMesh = AssetUtils.meshFromFile("models/cube-textures.obj");

		models.add(new ModelInstance(shipMesh, img));
		models.add(new ModelInstance(shipMesh, img).translate(2, 0, 0));
		models.add(new ModelInstance(shipMesh, img).translate(4, 0, 0));
		models.add(new ModelInstance(shipMesh, img).translate(6, 0, 0));
		models.add(new ModelInstance(cubeMesh, img2).translate(-1000, -1, -1000).scale(2000, 0.5f, 2000).setShininess(4));

		//directionalLights.add(Light.newDirectional(new Vector3(0, 1, 1), new Vector3(1, 1, 1)));
		//pointLights.add(Light.newPoint(new Vector3(1, 4, 1), new Vector3(0.2f, 0.2f, 0.2f)));
		spotLights.add(Light.newSpot(new Vector3(0, 5, 0), new Vector3((float) -Math.PI/2, 0, 0), new Vector3(0.5f, 0.5f, 0.5f),
				(float) Math.PI / 10));
		
		ambientLight = new Vector3(0.1f, 0.1f, 0.1f);
	}

	@Override
	protected void update() {
		updateAngle();
		
		Light spot = spotLights.get(0);
		spot.setPosition(new Vector3((float)Math.cos(angle)*3 + 3, 5, 0));
		
//		Light directional = directionalLights.get(0);
//		directional.setOrientation(new Vector3(0, 0, angle));
	}

	private void updateAngle() {
		angle += 0.05f;
		while (angle < 0)
			angle += Math.PI * 2;
		while (angle > Math.PI * 2)
			angle -= Math.PI * 2;
	}

	@Override
	protected Vector3 getAmbientLight() {
		return ambientLight;
	}
}
