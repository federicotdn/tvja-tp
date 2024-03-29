package com.tvja.render;

import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class PointShader extends Shader {
    private static final String FS = "shaders/phong-pointFS.glsl";

    public PointShader() {
        super(FS, false);
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, BaseModel model) {
        setCommonModelUniforms(view, model);
    }

    @Override
    protected void setLightUniforms(Light light, BaseModel model) {
        setUniform4fv("u_light_position", MathUtils.toVec4fPoint(light.getPosition()));
    }
}
