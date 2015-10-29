package com.tvja.render;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Hobbit on 10/28/15.
 */
public interface RenderableObject {
     void render(ShaderProgram shaderProgram, int primitiveType);
     void bind(int textureBind);
}
