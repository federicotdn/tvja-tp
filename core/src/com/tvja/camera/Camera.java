package com.tvja.camera;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class Camera implements OpenGLCamera {

	protected Matrix4 projection;
	protected Vector3 position;
	protected Vector3 orientation;

	public Camera() {
		position = new Vector3();
		orientation = new Vector3();
	}

	@Override
	public void setPosition(Vector3 pos) {
		position = pos;
	}

	@Override
	public Vector3 getPosition() {
		return position;
	}

	@Override
	public void setOrientation(Vector3 dir) {
		orientation = dir;
	}

	@Override
	public Vector3 getOrientation() {
		return orientation;
	}
	
	protected Matrix4 getRxMatrix() {
		return new Matrix4(new float[] {1, 0, 0, 0,
				0, (float)Math.cos(orientation.x), (float) Math.sin(orientation.x), 0,
				0, - (float) Math.sin(orientation.x), (float) Math.cos(orientation.x), 0,
				0, 0, 0, 1});
	}

	protected Matrix4 getRyMatrix() {
		return new Matrix4(new float[] {(float)Math.cos(orientation.y), 0, - (float)Math.sin(orientation.y), 0,
				0, 1, 0, 0,
				(float)Math.sin(orientation.y), 0, (float) Math.cos(orientation.y), 0,
				0, 0, 0, 1});
	}
	
	protected Matrix4 getRzMatrix() {
		return new Matrix4(new float[] {(float)Math.cos(orientation.z), (float)Math.sin(orientation.z), 0, 0,
				- (float)Math.sin(orientation.z), (float)Math.cos(orientation.z), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1});
	}
	
	public Matrix4 getTransMatrix() {
		return new Matrix4(
				new float[] { 1, 0, 0, 0, 
						0, 1, 0, 0, 
						0, 0, 1, 0, 
						position.x, position.y, position.z, 1 });
	}
	
	@Override
	public Matrix4 getViewProjection() {
		Matrix4 t = getTransMatrix();
		
		Matrix4 rx = getRxMatrix();
		Matrix4 ry = getRyMatrix();
		Matrix4 rz = getRzMatrix();
		
		Matrix4 r = rz.mul(ry).mul(rx);

		Matrix4 trs = t.mul(r);
		Matrix4 projCopy = new Matrix4(projection);
		return projCopy.mul(trs.inv());
	}
}
