package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSController;
import com.tvja.camera.OrthogonalCamera;
import com.tvja.camera.PerspectiveCamera;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.render.Shader;
import com.tvja.render.ViewWorldObject;

import java.util.ArrayList;
import java.util.List;

public abstract class TPGameBase extends ApplicationAdapter {

	private static final Vector3 COLOR_BLACK = new Vector3(0, 0, 0);

    private FPSController controller = new FPSController();
    protected ViewWorldObject<?> cam = new PerspectiveCamera();

    private Shader directionalShader;
    private Shader pointShader;
    private Shader spotShader;
    private Shader singleColorShader;

    final protected List<ModelInstance> models = new ArrayList<>();

    final protected List<Light> directionalLights = new ArrayList<>();
    final protected List<Light> spotLights = new ArrayList<>();
    final protected List<Light> pointLights = new ArrayList<>();
    
   // final private Map<Light, FrameBuffer> shadowMaps = new HashMap<>();
    
    protected abstract void init();
    protected abstract void update();
    protected abstract Vector3 getAmbientLight();
    
    @Override
    public void create() {
        setupGdx();

        directionalShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-directionalFS.glsl");
        pointShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-pointFS.glsl");
        spotShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-spotFS.glsl");
        singleColorShader = new Shader("shaders/defaultVS.glsl", "shaders/single-colorFS.glsl");
        
        init();
    }

    private void setupGdx() {
        Gdx.graphics.setDisplayMode(1000, 1000, false);
        Gdx.input.setCursorCatched(true);

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);    
    }

    @Override
    public void render() {
        checkExit();
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        controller.updatePositionOrientation(cam);
        updateCameraType();

        singleColorShader.render(cam, models, COLOR_BLACK);
        singleColorShader.render(cam, models, getAmbientLight());
        spotShader.render(cam, models, spotLights);
        pointShader.render(cam, models, pointLights);
        directionalShader.render(cam, models, directionalLights);
        
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

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
