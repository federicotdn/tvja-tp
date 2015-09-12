package com.tvja.camera;

public class OrthoFPSCamera extends OrthogonalCamera implements FPSControllableCamera {

	private static final float DEFAULT_WIDTH = 5.0f;
	private static final float DEFAULT_HEIGHT = 5.0f;
	private static final float DEFAULT_NEARZ = 0.1f;
	private static final float DEFAULT_FARZ = 100.0f;
	
	private FPSController controller;
	
	public OrthoFPSCamera(float nearZ, float farZ, float height, float width) {
		super(nearZ, farZ, height, width);
		controller = new FPSController();
	}
	
	public OrthoFPSCamera(float speed, float sensibility) {
		this(DEFAULT_NEARZ, DEFAULT_FARZ, DEFAULT_HEIGHT, DEFAULT_WIDTH);
		controller = new FPSController(speed, sensibility);
	}

	@Override
	public FPSController getController() {
		return controller;
	}

	@Override
	public void update() {
		controller.updatePositionOrientation(this);
	}
}
