package com.tvja.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSController;
import com.tvja.camera.OrthogonalCamera;
import com.tvja.camera.PerspectiveCamera;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.utils.AssetUtils;
import com.tvja.utils.MathUtils;

import java.util.LinkedList;
import java.util.List;

public class TestGame extends TPGameBase {

	private Texture img, img2;
	private Mesh shipMesh;
	private Mesh cubeMesh;
    private FPSController controller = new FPSController();

	private float angle = 0;
	
	private Vector3 ambientLight;
	
	private List<ModelInstance> ships = new LinkedList<>();
	
	@Override
	protected void init() {
		cam = new PerspectiveCamera();

		// Initialize assets
		img = AssetUtils.textureFromFile("models/ship.png");
		img2 = AssetUtils.textureFromFile("models/plain.jpg");

		shipMesh = AssetUtils.meshFromFile("models/ship.obj");
		cubeMesh = AssetUtils.meshFromFile("models/cube-textures.obj");

		models.add(new ModelInstance(shipMesh, img));

		ModelInstance mi = new ModelInstance(shipMesh, img);
		mi.translate(2, 0.2f, 0);
		models.add(mi);
		ships.add(mi);

		mi = new ModelInstance(shipMesh, img);
		mi.translate(4, 0.5f, 0);
		mi.setShininess(4);
		models.add(mi);
		ships.add(mi);
		
		mi = new ModelInstance(shipMesh, img);
		mi.translate(6, 1f, 0);
		models.add(mi);
		ships.add(mi);

		mi = new ModelInstance(cubeMesh, img2);
		mi.translate(-1000, -1, -1000);
		mi.scale(2000, 0.5f, 2000);
		mi.setShininess(4);
		models.add(mi);
		
		directionalLights.add(Light.newDirectional(new Vector3(-(float)(Math.PI/4), 3.5199971f, 0), new Vector3(0.5f, 0.5f, 0.5f)));
		pointLights.add(Light.newPoint(new Vector3(1, 5, 1), new Vector3(0.2f, 0.2f, 0.2f)));
		spotLights.add(Light.newSpot(new Vector3(0, 10, 0), new Vector3((float) -Math.PI/2, 0, 0), new Vector3(1,1,1),
				(float) Math.PI / 7));
		
		ambientLight = new Vector3(0.05f, 0.05f, 0.05f);
		
		Vector3 ori = new Vector3((float)Math.PI, (float)Math.PI/2, 0);
		System.out.println(ori);
		Vector3 dir = MathUtils.toDirection(ori);
		System.out.println(dir);
		System.out.println(MathUtils.toOrientation(dir));
	}

	@Override
	protected void update() {
        controller.updatePositionOrientation(cam);
        updateCameraType();
		
		updateAngle();

		Light spot = spotLights.get(0);
		
		if (Gdx.input.isKeyPressed(Keys.V)) {
			spot.setPosition(cam.getPosition());
			spot.setOrientation(cam.getOrientation());
			spot.model.setOrientation(spot.getOrientation());
			spot.model.setPosition(spot.getPosition());
			spot.model.rotate((float)(-Math.PI/2),0,0);
		}
		
		for (ModelInstance ship : ships) {
			ship.rotate(0, 0, 0.01f);
		}
	}

    private void updateCameraType() {
        if (Gdx.input.isKeyPressed(Keys.X)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new PerspectiveCamera();
            cam.setOrientation(ori);
            cam.setPosition(pos);
        } else if (Gdx.input.isKeyPressed(Keys.C)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new OrthogonalCamera();
            cam.setOrientation(ori);
            cam.setPosition(pos);
        }
    }	

	private void updateAngle() {
		angle += 0.01f;
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
