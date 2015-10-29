package com.tvja.render;

import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

/**
 * Created by Hobbit on 10/24/15.
 */
public class SingleColorShader extends Shader {
    private static final String VS = "shaders/defaultVS.glsl";
    private static final String FS = "shaders/single-colorFS.glsl";
    private static final Vector3 COLOR_BLACK = new Vector3(0, 0, 0);

    private Vector3 ambientColor;

    public SingleColorShader(Vector3 ambientColor) {
        super(VS, FS);
        this.ambientColor = ambientColor;
    }

    public SingleColorShader() {
        super(VS, FS);
        this.ambientColor = COLOR_BLACK;
    }

    @Override
    protected void setModelUniforms(ViewWorldObject view, BaseModel model) {
        setUniform4fv("u_ambient_color", MathUtils.toVec4fPoint(ambientColor));
    }

    public void setAmbientColor(Vector3 ambientColor) {
        this.ambientColor = ambientColor;
    }
}
