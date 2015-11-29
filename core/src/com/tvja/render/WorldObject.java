package com.tvja.render;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

/*
 * "WorldObject changes everything. It just works. Seamlessly." -Steve Jobs, 2014
 */
public abstract class WorldObject {
	private Vector3 position;
	private Vector3 orientation;
	private Vector3 scaled;
	
	private Matrix4 t;
	private Matrix4 r;
	private Matrix4 s;
	private Matrix4 trs;
	
	private boolean modified;
	
	public WorldObject() {
		position = new Vector3();
		orientation = new Vector3();
		scaled = new Vector3(1, 1, 1);
		t = new Matrix4();
		r = new Matrix4();
		s = new Matrix4();
		trs = new Matrix4();
		modified = false;
	}
	
	public WorldObject(Vector3 pos, Vector3 ori, Vector3 scale) {
		this();
		if (pos == null || ori == null || scale == null) {
			throw new IllegalArgumentException();
		}
		
		setPosition(pos);
		setOrientation(ori);
		setScale(scale);
	}
	
	public WorldObject(Vector3 pos, Vector3 ori) {
		this(pos, ori, new Vector3(1, 1, 1));
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}

	public void setPosition(Vector3 pos) {
		position = new Vector3();
		t = new Matrix4();
		translate(pos.x, pos.y, pos.z);
	}

	public Vector3 getOrientation() {
		return orientation.cpy();
	}
	
	public Vector3 getDirection() {
		return MathUtils.toDirection(orientation);
	}

	public void setOrientation(Vector3 ori) {
		orientation = new Vector3();
		r = new Matrix4();
        rotate(ori.x, ori.y, ori.z);
	}

	public Vector3 getScale() {
		return scaled.cpy();
	}

	public void setScale(Vector3 toScale) {
		scaled = new Vector3(1, 1, 1);
		s = new Matrix4();
		scale(toScale.x, toScale.y, toScale.z);
	}
	
    public void translate(float x, float y, float z) {
        modified = true;
        Vector3 translation = new Vector3(x, y, z);
        t.translate(translation);
        position.add(translation);
    }

    public void rotate(float x, float y, float z) {
        modified = true;
        r.rotateRad(new Vector3(0, 0, 1), z);
        r.rotateRad(new Vector3(0, 1, 0), y);
        r.rotateRad(new Vector3(1, 0, 0), x);
        orientation.add(new Vector3(x, y, z));
    }

    public void scale(float x, float y, float z) {
        modified = true;
        s.scale(x, y, z);
        scaled.crs(new Vector3(x, y, z));
    }

	public Matrix4 getR() {
		return r.cpy();
	}
	
	public Matrix4 getTRS() {
		if (modified) {
			modified = false;
			trs = new Matrix4(t);
			trs.mul(r).mul(s);
		}
		
		return trs.cpy();
	}
}
