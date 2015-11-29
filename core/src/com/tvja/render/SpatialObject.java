package com.tvja.render;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

public class SpatialObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Vector3 position;
	private Vector3 orientation;
	private Vector3 scaled;
	
	public SpatialObject() {
		position = new Vector3();
		orientation = new Vector3();
		scaled = new Vector3(1, 1, 1);
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}

	public void setPosition(Vector3 pos) {
		position = pos;
	}
	
	public Vector3 getOrientation() {
		return orientation.cpy();
	}
	
	public void setOrientation(Vector3 ori) {
		orientation = ori;
	}
	
	public Vector3 getDirection() {
		return MathUtils.toDirection(orientation);
	}
	
	public Vector3 getScale() {
		return scaled.cpy();
	}
	
	public void setScale(Vector3 toScale) {
		scaled = toScale;
	}
	
    public void translate(float x, float y, float z) {
        position.add(new Vector3(x, y, z));
    }

    public void rotate(float x, float y, float z) {
        orientation.add(new Vector3(x, y, z));
    }

    public void scale(float x, float y, float z) {
        scaled.crs(new Vector3(x, y, z));
    }
}
