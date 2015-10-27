package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.tvja.utils.AssetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hobbit on 10/25/15.
 */
public class DirectionalShadowShader extends DirectionalShader {
    private static final String VS = "shaders/default-shadowVS.glsl";
    private static final String FS = "shaders/shadow-directionalFS.glsl";

    private FrameBuffer frameBuffer;
    private Shader depthShader;
    private SingleColorShader earlyZShader;

    public DirectionalShadowShader() {
        super(VS, FS);
        depthShader = new Shader("shaders/defaultVS.glsl", "shaders/depthFS.glsl");
        earlyZShader = new SingleColorShader();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 4096, 4096, true);
    }

    @Override
    protected void setLightUniforms(Light light, ModelInstance model) {
        super.setLightUniforms(light, model);
        setUniformMat4("u_light_bias_mvp", biasMat.cpy().mul(light.getViewProjection().mul(model.getTRS())));
    }

    @Override
    protected void setShadowUniforms(GLFrameBuffer<? extends GLTexture> shadowMap) {
        FrameBuffer fb = (FrameBuffer) shadowMap;
        Texture tt = fb.getColorBufferTexture();

        Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
        tt.bind();

        setUniformi("u_shadow_map", 2);
    }
    
    private Shader fullscreenShader = new Shader("shaders/fullscreenVS.glsl", "shaders/fullscreenFS.glsl" );
    private ModelInstance fsQuad = new ModelInstance(AssetUtils.loadFullScreenQuad(), null);

    @Override
    protected Map<Light, GLFrameBuffer<? extends GLTexture>> setUpShader(List<ModelInstance> models, List<Light> lights) {
        frameBuffer.begin();
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);
        earlyZShader.render(lights.get(0), models);
        depthShader.render(lights.get(0), models, null);
        frameBuffer.end();

        Map<Light, GLFrameBuffer<? extends GLTexture>> map = new HashMap<>();
        map.put(lights.get(0), frameBuffer);

//	    FrameBuffer fb = frameBuffer;
//	    Texture tt = fb.getColorBufferTexture();
//
//	    Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
//	    tt.bind();
//
//	    setUniformi("u_shadow_map", 2);
//
//	    fullscreenShader.renderFullscreen(fsQuad, 2);
        
        return map;
    }
}
