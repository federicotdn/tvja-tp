package com.tvja.render;

import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class SpotShader extends Shader {
    private static final String VS = "shaders/defaultVS.glsl";
    private static final String FS = "shaders/phong-spotFS.glsl";

    public SpotShader() {
        super(VS, FS);
    }

    protected SpotShader(String vs, String fs) {
        super(vs, fs);
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, BaseModel model) {
        setCommonModelUniforms(view, model);
    }

    @Override
    protected void setLightUniforms(Light light, BaseModel model) {
        setUniform4fv("u_light_direction", MathUtils.toVec4fDirection(light.getDirection()));
        setUniformf("u_cutoff_angle", light.getAngle().get());
        setUniform4fv("u_light_position", MathUtils.toVec4fPoint(light.getPosition()));
    }
}
