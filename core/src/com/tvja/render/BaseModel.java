package com.tvja.render;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Hobbit on 10/28/15.
 */
public abstract class BaseModel extends WorldObject {
     private static final Integer DEFAULT_SHININESS = 3;
     private Integer shininess;

     public BaseModel() {
          shininess = null;
     }

     public Integer getShininess() {
          return shininess == null ? DEFAULT_SHININESS : shininess;
     }

     public BaseModel setShininess(Integer sh) {
          shininess = sh;
          return this;
     }

     public abstract void render(ShaderProgram shaderProgram, int primitiveType, ViewWorldObject view);
     public abstract void bind();
}
