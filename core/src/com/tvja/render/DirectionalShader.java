package com.tvja.render;

import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class DirectionalShader extends Shader {
    private static final String VS = "shaders/defaultVS.glsl";
    private static final String FS = "shaders/phong-directionalFS.glsl";

    public DirectionalShader() {
       super(VS, FS);
    }


    protected DirectionalShader(String vs, String fs) {
        super(vs, fs);
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, ModelInstance model) {
        setCommonModelUniforms(view, model);
    }

    @Override
    protected void setLightUniforms(Light light, ModelInstance model) {
        setUniform4fv("u_light_direction", MathUtils.toVec4fDirection(light.getDirection()));
    }
}
