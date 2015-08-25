package com.tvja.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class FPSCamera extends PerspectiveCamera implements FPSControllableCamera {

	private static final float DEFAULT_FOVX = 60.0f;
	private static final float DEFAULT_FOVY = 60.0f;
	private static final float DEFAULT_NEARZ = 0.1f;
	private static final float DEFAULT_FARZ = 100.0f;
	
	private FPSController controller;

	public FPSCamera(float nearZ, float farZ, float fovX, float fovY) {
		super(nearZ, farZ, fovX, fovY);
		controller = new FPSController();
	}
	
	public FPSCamera(float speed, float sensibility) {
		this(DEFAULT_NEARZ, DEFAULT_FARZ, DEFAULT_FOVX, DEFAULT_FOVY);
		controller = new FPSController(speed, sensibility);
	}
	
	@Override
	protected Matrix4 getRzMatrix() {
		return new Matrix4(); // Never rotate around Z-axis.
	}
	
	@Override
	public void update() {
		controller.updatePositionOrientation(position, orientation);
	}

	@Override
	public FPSController getController() {
		return controller;
	}
}
