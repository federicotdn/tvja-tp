package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.tvja.render.*;

import java.util.*;

public abstract class TPGameBase extends ApplicationAdapter {
    final protected List<BaseModel> models = new ArrayList<>();
    final protected Map<String, List<BaseModel>> groupedModels = new HashMap<>();
    final protected List<AnimationModel> animatedModels = new LinkedList<>();
    final protected List<Light> directionalLights = new ArrayList<>();
    final protected List<Light> spotLights = new ArrayList<>();
    final protected List<Light> pointLights = new ArrayList<>();
    protected ViewWorldObject cam;
    private Shader directionalShader;
    private Shader pointShader;
    private Shader spotShader;
    private Shader ambientShader;
    private Shader earlyZShader;

    protected abstract void init();

    protected abstract void update();

    protected abstract Vector3 getAmbientLight();

    @Override
    public void create() {
        setupGdx();
        init();

        directionalShader = new DirectionalShadowShader();
        pointShader = new PointShader();
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
    }

    @Override
    public void render() {
        checkExit();
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        earlyZShader.render(cam, groupedModels);

        ambientShader.render(cam, groupedModels);

        spotShader.render(cam, groupedModels, spotLights);
//        pointShader.render(cam, models, pointLights);
        directionalShader.render(cam, groupedModels, directionalLights);
    }

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void groupModels() {
        groupedModels.clear();
        for (BaseModel bm : models) {
            if (!groupedModels.containsKey(bm.getVS())) {
                groupedModels.put(bm.getVS(), new LinkedList<>());
            }
            groupedModels.get(bm.getVS()).add(bm);
        }
    }
}
