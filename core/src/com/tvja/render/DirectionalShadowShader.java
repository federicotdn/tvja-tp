package com.tvja.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hobbit on 10/25/15.
 */
public class DirectionalShadowShader extends DirectionalShader {
    private static final String FS = "shaders/shadow-directionalFS.glsl";

    private FrameBuffer frameBuffer;
    private Shader depthShader;
    private SingleColorShader earlyZShader;

    public DirectionalShadowShader() {
        super(FS, true);
        depthShader = new Shader("shaders/depthFS.glsl", false);
        earlyZShader = new SingleColorShader();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 4096, 4096, true);
    }

    @Override
    protected void setLightUniforms(Light light, BaseModel model) {
        super.setLightUniforms(light, model);
        setUniformMat4("u_light_bias_mvp", biasMat.cpy().mul(light.getViewProjection().mul(model.getTRS())));
    }

    @Override
    protected void setShadowUniforms(FrameBuffer fb) {
        Texture tt = fb.getColorBufferTexture();

        Gdx.gl20.glActiveTexture(Gdx.gl20.GL_TEXTURE2);
        tt.bind();

        setUniformi("u_shadow_map", 2);
    }

    @Override
    protected Map<Light, FrameBuffer> setUpShader(List<? extends BaseModel> models, List<Light> lights) {
        frameBuffer.begin();
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);
        earlyZShader.render(lights.get(0), models);
        depthShader.render(lights.get(0), models, null);
        frameBuffer.end();

        Map<Light, FrameBuffer> map = new HashMap<>();
        map.put(lights.get(0), frameBuffer);
        
        return map;
    }
}
