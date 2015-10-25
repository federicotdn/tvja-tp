package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.tvja.camera.FPSController;
import com.tvja.camera.OrthogonalCamera;
import com.tvja.camera.PerspectiveCamera;
import com.tvja.render.*;
import com.tvja.utils.AssetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TPGameBase extends ApplicationAdapter {
    private FPSController controller = new FPSController();
    protected ViewWorldObject cam = new PerspectiveCamera();

    private Shader directionalShader;
    private Shader pointShader;
    private Shader spotShader;
    private Shader ambientShader;
    private Shader earlyZShader;

    private Shader shadowMapShader;
    private Shader fullscreenShader;
    private Shader depthShader;

    private ModelInstance fsQuad;

    final protected List<ModelInstance> models = new ArrayList<>();

    final protected List<Light> directionalLights = new ArrayList<>();
    final protected List<Light> spotLights = new ArrayList<>();
    final protected List<Light> pointLights = new ArrayList<>();
    
    final private Map<Light, FrameBuffer> shadowMaps = new HashMap<>();
    
    protected abstract void init();
    protected abstract void update();
    protected abstract Vector3 getAmbientLight();
    
    @Override
    public void create() {
        setupGdx();

        directionalShader = new DirectionalShadowShader();
        pointShader = new PointShader();
        spotShader = new SpotShadowShader();

//        shadowMapShader = new Shader("shaders/default-shadowVS.glsl", "shaders/shadow-mapFS.glsl");
//        fullscreenShader = new Shader("shaders/fullscreenVS.glsl", "shaders/fullscreenFS.glsl");
//        depthShader = new Shader("shaders/defaultVS.glsl", "shaders/depthFS.glsl");
        
        fsQuad = new ModelInstance(AssetUtils.loadFullScreenQuad(), null);
        
        init();
        setupShadowMaps();

        ambientShader = new SingleColorShader(getAmbientLight());
        earlyZShader = new SingleColorShader();
    }
    
    private void setupShadowMaps() {
    	for (Light light : spotLights) {
    		shadowMaps.put(light, new FrameBuffer(Format.RGB888, 1024, 1024, true));
    	}
    }

    private void setupGdx() {
        Gdx.graphics.setDisplayMode(1024, 1024, false);
        Gdx.input.setCursorCatched(false);

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
        Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);

        controller.updatePositionOrientation(cam);
        updateCameraType();

//        earlyZShader.render(cam, models);
//        ambientShader.render(cam, models);

        spotShader.render(cam, models, spotLights);
//        pointShader.render(cam, models, pointLights);
//        directionalShader.render(cam, models, directionalLights);

//        //for (Light light : spotLights) {
//        	Light light = spotLights.get(0);
//        	FrameBuffer fb = shadowMaps.get(light);
//        	fb.begin();
//        	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
////        	singleColorShader.render(light, models, COLOR_BLACK);
//        	depthShader.render(light, models, (Vector3)null);
//        	fb.end();
//
//        	Texture tt = fb.getColorBufferTexture();
//
//        	Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
//        	tt.bind();
//        	//fullscreenShader.renderFullscreen(fsQuad, 2);
//        	Gdx.gl20.glBlendFunc(GL20.GL_ONE, GL20.GL_SRC_ALPHA);
//        	shadowMapShader.renderShadow(light, cam, models, 2);
//        //}
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
