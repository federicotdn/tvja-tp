package com.tvja.camera;

import com.badlogic.gdx.math.Matrix4;
import com.tvja.render.WorldObject;

public abstract class Camera extends WorldObject<Camera> implements OpenGLCamera {
	protected Matrix4 projection;
	
	@Override
	protected Camera getThis() {
		return this;
	}
	
	@Override
	public Matrix4 getViewProjection() {
		Matrix4 projCopy = new Matrix4(projection);
		return projCopy.mul(getTRS().inv());
	}
}
