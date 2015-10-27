package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.AssetUtils;
import com.tvja.utils.MathUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hobbit on 10/27/15.
 */
public class PointShadowShader extends Shader {
    private static final String VS = "shaders/default-shadowVS.glsl";
    private static final String FS = "shaders/shadow-pointFS.glsl";

    private FrameBufferCubemap fb;
    private Shader depthShader;
    private SingleColorShader earlyZShader;
    private HashMap<Cubemap.CubemapSide, Vector3> orientations;

    public PointShadowShader() {
        super(VS, FS);
        depthShader = new Shader("shaders/defaultVS.glsl", "shaders/depthFS.glsl");
        earlyZShader = new SingleColorShader();
        fb = new FrameBufferCubemap(Pixmap.Format.RGB888, 2048, 2048, true);
        orientations = new HashMap<>();
    }

    @Override
    protected void setLightUniforms(Light light, ModelInstance model) {
        super.setLightUniforms(light, model);
        setUniformMat4("u_light_bias_mvp", biasMat.cpy().mul(light.getViewProjection().mul(model.getTRS())));
    }

    @Override
    protected void setShadowUniforms(GLFrameBuffer<? extends  GLTexture> shadowMap) {
        FrameBufferCubemap fb = (FrameBufferCubemap) shadowMap;
        Cubemap cubeMap = fb.getColorBufferTexture();

        Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
        cubeMap.bind();

        setUniformi("u_shadow_map", 2);
    }

    private Shader fullscreenShader = new Shader("shaders/fullscreenVS.glsl", "shaders/fullscreenFS.glsl" );
    private ModelInstance fsQuad = new ModelInstance(AssetUtils.loadFullScreenQuad(), null);

    @Override
    protected Map<Light, GLFrameBuffer<? extends GLTexture>> setUpShader(List<ModelInstance> models, List<Light> lights) {
        fb.begin();
        int i = 0;
        while (fb.nextSide()) {
            Cubemap.CubemapSide side = fb.getSide();

            Vector3 direction = new Vector3();
            if (!orientations.containsKey(side)) {
//                Vector3 ori;
//                switch (side) {
//                    case PositiveX:
//                        ori = new Vector3(0,(float) Math.PI/2,0);
//                        break;
//                    case NegativeX:
//                        ori = new Vector3(0,-(float) Math.PI/2,0);
//                        break;
//                    case PositiveY:
//                        ori = new Vector3((float) Math.PI/2,0,0);
//                        break;
//                    case NegativeY:
//                        ori = new Vector3(-(float) Math.PI/2,0,0);
//                        break;
//                    case PositiveZ:
//                }
                Vector3 ori =  MathUtils.toOrientation(side.getDirection(direction));
                ori.z = 0;
                        orientations.put(side, ori);
                System.out.println("------------------------------------");
                System.out.println(side.getDirection(direction));
                System.out.println(orientations.get(side));
            }
            Light l = Light.newPointSideLight(lights.get(0).getPosition(), orientations.get(side));
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            earlyZShader.render(l, models);
            depthShader.render(l, models, null);
            i++;
        }
        fb.end();

        Map<Light, GLFrameBuffer<? extends  GLTexture>> map = new HashMap<>();
        map.put(lights.get(0), fb);

//        Cubemap cubeMap = fb.getColorBufferTexture();
//
//        Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
//        cubeMap.bind();
//
//        setUniformi("u_shadow_map", 2);
//
//        fullscreenShader.renderFullscreen(fsQuad, 2);

        return map;
    }
}
