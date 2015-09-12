package com.tvja.camera;

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
	public void update() {
		controller.updatePositionOrientation(this);
	}

	@Override
	public FPSController getController() {
		return controller;
	}
}
