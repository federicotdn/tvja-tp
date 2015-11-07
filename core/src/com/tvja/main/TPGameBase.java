package com.tvja.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.tvja.render.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class TPGameBase extends ApplicationAdapter {
    final protected List<ModelInstance> models = new ArrayList<>();
    final protected List<AnimationModel> animatedModels = new LinkedList<>();
    final protected List<Light> directionalLights = new ArrayList<>();
    final protected List<Light> spotLights = new ArrayList<>();
    final protected List<Light> pointLights = new ArrayList<>();
    protected ViewWorldObject cam;
    private Shader directionalShader;
    private Shader animatedShader;
    private Shader pointShader;
    private Shader spotShader;
    private Shader ambientShader;
    private Shader earlyZShader;

    private SingleColorShader ambientShader2;
    private Shader earlyZShader2;

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



        ShaderFunction animatedShaderFunction = getShaderFunction();
        earlyZShader2 = new SingleColorShader("shaders/animated-shadow-VS.glsl", "shaders/single-colorFS.glsl", animatedShaderFunction);
        ambientShader2 = new SingleColorShader("shaders/animated-shadow-VS.glsl", "shaders/single-colorFS.glsl", animatedShaderFunction);
        ambientShader2.setAmbientColor(getAmbientLight());
        animatedShader = new DirectionalShader("shaders/animated-shadow-VS.glsl", "shaders/phong-directionalFS.glsl",
                animatedShaderFunction);

    }


    private ShaderFunction getShaderFunction() {
        return ((model, shaderProgram) -> {
//            AnimationModel aModel = (AnimationModel) model;
//            Array<Renderable> renderables = new Array<Renderable>();
//            final Pool<Renderable> pool = new Pool<Renderable>() {
//                @Override
//                protected Renderable newObject() {
//                    return new Renderable();
//                }
//
//                @Override
//                public Renderable obtain() {
//                    Renderable renderable = super.obtain();
////                    renderable.lights = null;
//                    renderable.material = null;
//                    renderable.mesh = null;
//                    renderable.shader = null;
//                    return renderable;
//                }
//            };
//            aModel.getModelInstance().getRenderables(renderables, pool);
//            Matrix4 idtMatrix = new Matrix4().idt();
//            float[] bones = new float[32 * 16];
//            for (int i = 0; i < bones.length; i++)
//                bones[i] = idtMatrix.val[i % 16];
//            for (Renderable render : renderables) {
////                mvpMatrix.set(g.cam.combined);
////                mvpMatrix.mul(render.worldTransform);
////                charShader.setUniformMatrix("u_mvpMatrix", mvpMatrix);
////                nMatrix.set(g.cam.view);
////                nMatrix.mul(render.worldTransform);
////                charShader.setUniformMatrix("u_modelViewMatrix", nMatrix);
////                nMatrix.inv();
////                nMatrix.tra();
////                charShader.setUniformMatrix("u_normalMatrix", nMatrix);
////                StaticVariables.tempMatrix.idt();
//                for (int i = 0; i < bones.length; i++) {
//                    final int idx = i / 16;
//                    bones[i] = (render.bones == null || idx >= render.bones.length || render.bones[idx] == null) ?
//                            idtMatrix.val[i % 16] : render.bones[idx].val[i % 16];
//                }
//                shaderProgram.setUniformMatrix4fv("u_bones", bones, 0, bones.length);
//            }
//
//            aModel.setRenderables(renderables);

        });
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

        earlyZShader.render(cam, models);
        earlyZShader2.render(cam, animatedModels);

        ambientShader.render(cam, models);
        ambientShader2.render(cam, animatedModels);

        spotShader.render(cam, models, spotLights);
        //pointShader.render(cam, models, pointLights);
        directionalShader.render(cam, models, directionalLights);



        animatedShader.render(cam, animatedModels, directionalLights);
        // Hack! Change later

        List<BaseModel> m = new LinkedList<>();
        m.add(directionalLights.get(0).model);
        ((SingleColorShader) ambientShader).setAmbientColor(new Vector3(1, 0, 0));
        ambientShader.render(cam, m);
        ((SingleColorShader) ambientShader).setAmbientColor(getAmbientLight());
    }

    private void checkExit() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
}
