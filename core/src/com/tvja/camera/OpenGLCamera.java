package com.tvja.camera;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public interface OpenGLCamera {
	public Matrix4 getViewProjection();
	public void setPosition(Vector3 pos);
	public Vector3 getPosition();
	public void setOrientation(Vector3 dir);
	public Vector3 getOrientation();
}
