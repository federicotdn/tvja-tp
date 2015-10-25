package com.tvja.render;

import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class SingleColorShader extends Shader {
    private static final String VS = "shaders/defaultVS.glsl";
    private static final String FS = "shaders/single-colorFS.glsl";

    private Vector3 ambientColor;

    public SingleColorShader(Vector3 ambientColor) {
        super(VS, FS);
        this.ambientColor = ambientColor;
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, ModelInstance model) {
        setUniform4fv("u_ambient_color", MathUtils.toVec4fPoint(ambientColor));
    }

    @Override
    protected void setLightUniforms(Light light) {

    }

    public void setAmbientColor(Vector3 ambientColor) {
        this.ambientColor = ambientColor;
    }
}
