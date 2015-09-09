package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSCamera;
import com.tvja.camera.FPSControllableCamera;
import com.tvja.camera.OrthoFPSCamera;
import com.tvja.render.Light;
import com.tvja.render.ModelInstance;
import com.tvja.render.Shader;

import java.util.ArrayList;
import java.util.List;

public abstract class TPGameBase extends ApplicationAdapter {

    FPSControllableCamera cam = new FPSCamera(0.1f, 0.01f);

    Shader directionalShader;
    Shader pointShader;
    Shader spotShader;
    Shader earlyZShader;

    List<ModelInstance> models = new ArrayList<>();

    List<Light> directionalLights = new ArrayList<>();
    List<Light> spotLights = new ArrayList<>();
    List<Light> pointLights = new ArrayList<>();
    
    protected abstract void init();
    protected abstract void update();

    @Override
    public void create() {
        setupGdx();

        directionalShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-directionalFS.glsl");
        pointShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-pointFS.glsl");
        spotShader = new Shader("shaders/defaultVS.glsl", "shaders/phong-spotFS.glsl");
        earlyZShader = new Shader("shaders/defaultVS.glsl", "shaders/early-zFS.glsl");
        
        init();
    }

    private void setupGdx() {
        Gdx.graphics.setDisplayMode(1000, 1000, false);
        Gdx.input.setCursorCatched(true);

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_DST_COLOR);    
    }

    @Override
    public void render() {
        checkExit();
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();
        updateCameraType();

        earlyZShader.render(cam, models, null);
        spotShader.render(cam, models, spotLights);
        pointShader.render(cam, models, pointLights);
        directionalShader.render(cam, models, directionalLights);
    }

    private void updateCameraType() {
        if (Gdx.input.isKeyPressed(Keys.X)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new FPSCamera(0.1f, 0.01f);
            cam.setOrientation(ori);
            cam.setPosition(pos);
        } else if (Gdx.input.isKeyPressed(Keys.C)) {
            Vector3 pos = cam.getPosition();
            Vector3 ori = cam.getOrientation();
            cam = new OrthoFPSCamera(0.1f, 0.01f);
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
