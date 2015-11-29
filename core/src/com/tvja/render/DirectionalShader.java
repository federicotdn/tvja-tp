package com.tvja.render;

import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class DirectionalShader extends Shader {
    private static final String FS = "shaders/phong-directionalFS.glsl";

    public DirectionalShader() {
       super(FS, false);
    }

    protected DirectionalShader(String fs, boolean useShadow) {
        super(fs, useShadow);
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, BaseModel model) {
        setCommonModelUniforms(view, model);
    }

    @Override
    protected void setLightUniforms(Light light, BaseModel model) {
        setUniform4fv("u_light_direction", MathUtils.toVec4fDirection(light.getDirection()));
    }
}
