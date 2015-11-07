package com.tvja.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSController;
import com.tvja.camera.OrthogonalCamera;
import com.tvja.camera.PerspectiveCamera;
import com.tvja.render.AnimationModel;
import com.tvja.render.BaseModel;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.utils.AssetUtils;

import java.util.LinkedList;
import java.util.List;

public class TestGame extends TPGameBase {

	private Texture img, img2;
	private Mesh shipMesh;
	private Mesh cubeMesh;
    private FPSController controller = new FPSController();
	private AssetManager assetManager =  new AssetManager();
	private boolean loading;

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


		directionalLights.add(Light.newDirectional(new Vector3(-(float) (Math.PI / 4), 3.5199971f, 0), new Vector3(0.5f, 0.5f, 0.5f)));
		pointLights.add(Light.newPoint(new Vector3(1, 5, 1), new Vector3(0.2f, 0.2f, 0.2f)));
		spotLights.add(Light.newSpot(new Vector3(0, 10, 0), new Vector3((float) -Math.PI / 2, 0, 0), new Vector3(1, 1, 1),
				(float) Math.PI / 7));
		
		ambientLight = new Vector3(0.05f, 0.05f, 0.05f);
		
		// move to another scene later
		loading = true;
		assetManager.load("models/Dave.g3db", Model.class);
	}

	@Override
	protected void update() {
		if (loading && assetManager.update()) {
			loading = false;
			Model model = assetManager.get("models/Dave.g3db", Model.class);
			Texture tex = AssetUtils.textureFromFile("models/uv_dave_mapeo.jpg");

			com.badlogic.gdx.graphics.g3d.ModelInstance instance = new com.badlogic.gdx.graphics.g3d.ModelInstance(model);
			instance.transform.scale(0.2f, 0.2f, 0.2f);
			instance.transform.translate(0, 0, -5);

			AnimationModel animationModel = new AnimationModel(instance, tex);
			animationModel.setShininess(5);
//			animationModel.setPosition(new Vector3(0, 0.8f, 7));
//			animationModel.setScale(new Vector3(0.01f, 0.01f, 0.01f));
			animationModel.rotate(0, (float)Math.PI, 0);
			animatedModels.add(animationModel);
		}

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
		
		for (BaseModel ship : ships) {
			ship.rotate(0, 0, 0.01f);
		}

		for (AnimationModel aModel : animatedModels) {
			aModel.getAnimationController().update(Gdx.graphics.getDeltaTime());
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
