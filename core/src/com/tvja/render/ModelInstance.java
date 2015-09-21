package com.tvja.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;

public class ModelInstance extends WorldObject {
	
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
}
