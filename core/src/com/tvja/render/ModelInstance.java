package com.tvja.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ModelInstance extends WorldObject implements RenderableObject {
	
	private static final Integer DEFAULT_SHININESS = 3;
	
    private Mesh mesh;
    private Texture tex;
    private Integer shininess;

    public ModelInstance(Mesh mesh, Texture tex) {
        this.mesh = mesh;
        this.tex = tex;
        shininess = null;
    }
    
    public ModelInstance setShininess(Integer sh) {
    	shininess = sh;
    	return this;
    }
    
    public Integer getShininess() {
    	return shininess == null ? DEFAULT_SHININESS : shininess;
    }

    public Mesh getMesh() {
        return mesh;
    }
    
    public Texture getTex() {
        return tex;
    }


    @Override
    public void render(ShaderProgram shaderProgram, int primitiveType) {
        mesh.render(shaderProgram, primitiveType);
    }

    @Override
    public void bind(int textureBind) {
        tex.bind(textureBind);
    }
}
