package com.tvja.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ModelInstance {
	
	private static final Integer DEFAULT_SHININESS = 3;
	
    private Mesh mesh;
    private Texture tex;
    private Matrix4 t;
    private Matrix4 r;
    private Matrix4 s;
    private Matrix4 trs;
    private boolean modified;
    private Integer shininess;

    public ModelInstance(Mesh mesh, Texture tex) {
        this.mesh = mesh;
        this.tex = tex;
        t = new Matrix4();
        r = new Matrix4();
        s = new Matrix4();
        trs = new Matrix4();
        modified = false;
        shininess = null;
    }
    
    public ModelInstance setShininess(Integer sh) {
    	shininess = sh;
    	return this;
    }
    
    public Integer getShininess() {
    	return shininess == null ? DEFAULT_SHININESS : shininess;
    }

    public Matrix4 getR() {
        return r.cpy();
    }

    public ModelInstance translate(float x, float y, float z) {
        modified = true;
        t.translate(new Vector3(x, y, z));
        return this;
    }

    public ModelInstance rotate(float x, float y, float z) {
        modified = true;
        r.rotateRad(new Vector3(1, 0, 0), x);
        r.rotateRad(new Vector3(0, 1, 0), y);
        r.rotateRad(new Vector3(0, 0, 1), z);
        return this;
    }

    public ModelInstance scale(float x, float y, float z) {
        modified = true;
        s.scale(x, y, z);

        return this;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Texture getTex() {
        return tex;
    }

    public Matrix4 getTRS() {
        if (modified) {
            modified = false;
            trs = new Matrix4(t);
            trs.mul(r).mul(s);
        }

        return trs;
    }
}
