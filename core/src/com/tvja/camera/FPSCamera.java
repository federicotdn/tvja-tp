package com.tvja.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class FPSCamera extends PerspectiveCamera {

	private static final float DEFAULT_FOVX = 60.0f;
	private static final float DEFAULT_FOVY = 60.0f;
	private static final float DEFAULT_NEARZ = 0.1f;
	private static final float DEFAULT_FARZ = 100.0f;
	
	private float speed;
	private float sensibility;

	public FPSCamera(float nearZ, float farZ, float fovX, float fovY) {
		super(nearZ, farZ, fovX, fovY);
	}
	
	public FPSCamera(float speed, float sensibility) {
		this(DEFAULT_NEARZ, DEFAULT_FARZ, DEFAULT_FOVX, DEFAULT_FOVY);
		this.speed = speed;
		this.sensibility = sensibility;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getSensibility() {
		return sensibility;
	}

	public void setSensibility(float sensibility) {
		this.sensibility = sensibility;
	}
	
	@Override
	protected Matrix4 getRzMatrix() {
		return new Matrix4(); // Never rotate around Z-axis.
	}
	
	private Vector3 getForward() {
		Vector3 forward = new Vector3(0, 0, -1);
		//forward.rot(getRyMatrix()).rot(getRxMatrix());
		forward.rotateRad(new Vector3(1, 0, 0), orientation.x);
		forward.rotateRad(new Vector3(0, 1, 0), orientation.y);
		return forward.nor();
	}
	
	private Vector3 getRight() {
		Vector3 right = new Vector3(-1, 0, 0);
		//right.rot(getRyMatrix()).rot(getRxMatrix());
		right.rotateRad(new Vector3(1, 0, 0), orientation.x);
		right.rotateRad(new Vector3(0, 1, 0), orientation.y);
		return right.nor();
	}
	
	private void updatePosition() {
		if (Gdx.input.isKeyPressed(Keys.W)) {
			position.add(getForward().scl(speed));
		} else if (Gdx.input.isKeyPressed(Keys.S)) {
			position.add(getForward().scl(-speed));
		}
		
		if (Gdx.input.isKeyPressed(Keys.D)) {
			position.add(getRight().scl(-speed));
		} else if (Gdx.input.isKeyPressed(Keys.A)) {
			position.add(getRight().scl(speed));
		}	
	}
	
	private void correctAngles() {
		while (orientation.y < 0)
			orientation.y += Math.PI * 2;
		while (orientation.y > Math.PI * 2)
			orientation.y -= Math.PI * 2;
		
		float maxX = (float)(Math.PI / 2);
		
		if (orientation.x > maxX)
			orientation.x = maxX;
		else if (orientation.x < -maxX)
			orientation.x = -maxX;
	}
	
	private void updateOrientation() {
		int dx = Gdx.input.getDeltaX();
		int dy = Gdx.input.getDeltaY();
		
		orientation.y += ((float)dx) * -sensibility;
		orientation.x += ((float)dy) * -sensibility;
		
		correctAngles();
	}
	
	public void update() {
		updateOrientation();
		updatePosition();
	}
}
