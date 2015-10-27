package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.tvja.render.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TPGameBase extends ApplicationAdapter {
    protected ViewWorldObject cam;

    private Shader directionalShader;
    private Shader pointShader;
    private Shader spotShader;
    private Shader ambientShader;
    private Shader earlyZShader;

    final protected List<ModelInstance> models = new ArrayList<>();

    final protected List<Light> directionalLights = new ArrayList<>();
    final protected List<Light> spotLights = new ArrayList<>();
    final protected List<Light> pointLights = new ArrayList<>();
    
    protected abstract void init();
    protected abstract void update();
    protected abstract Vector3 getAmbientLight();
    
    @Override
    public void create() {
        setupGdx();
        init();

        directionalShader = new DirectionalShadowShader();
        pointShader = new PointShadowShader();
        spotShader = new SpotShadowShader();
        
        ambientShader = new SingleColorShader(getAmbientLight());
        earlyZShader = new SingleColorShader();
    }


    private void setupGdx() {
        Gdx.graphics.setDisplayMode(1024, 1024, false);
        Gdx.input.setCursorCatched(true);

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        	
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_CUBE_MAP);
    }

    @Override
    public void render() {
        checkExit();
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        earlyZShader.render(cam, models);
        ambientShader.render(cam, models);

//        spotShader.render(cam, models, spotLights);
        pointShader.render(cam, models, pointLights);
//        directionalShader.render(cam, models, directionalLights);

        // Hack! Change later

//        List<ModelInstance> m = new LinkedList<>();
//        m.add(directionalLights.get(0).model);
//        ((SingleColorShader)ambientShader).setAmbientColor(new Vector3(1,0,0));
//        ambientShader.render(cam, m);
//        ((SingleColorShader)ambientShader).setAmbientColor(getAmbientLight());
    }

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
