package com.tvja.main;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.utils.AssetUtils;

public class TestGame extends TPGameBase {

	Texture img, img2;
	Mesh shipMesh;
	Mesh cubeMesh;

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
		models.add(new ModelInstance(cubeMesh, img2).translate(-1000, -1, -1000).scale(2000, 0.5f, 2000));

		directionalLights.add(Light.newDirectional(new Vector3(1, 1, 1), new Vector3(1, 1, 1)));
		pointLights.add(Light.newPoint(new Vector3(1, 4, 1), new Vector3(1, 1, 1)));
		spotLights.add(Light.newSpot(new Vector3(0, 5, 0), new Vector3(0, 1, 0), new Vector3(0.8f, 0.8f, 0.8f),
				(float) Math.PI / 10));

	}

	@Override
	protected void update() {
		

	}

}
