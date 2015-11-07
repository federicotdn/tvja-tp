package com.tvja.render;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Hobbit on 11/6/15.
 */
public interface ShaderFunction {
    void apply(BaseModel model, ShaderProgram shaderProgram);
}
