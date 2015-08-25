package com.tvja.camera;

import com.badlogic.gdx.math.Matrix4;

public class PerspectiveCamera extends Camera {
	public PerspectiveCamera(float nearZ, float farZ, float fovX, float fovY) {
		projection = createPerspectiveProjection(nearZ, farZ, fovX, fovY);
	}
	
	private Matrix4 createPerspectiveProjection(float nearZ, float farZ, float fovX, float fovY) {
		
		return new Matrix4(new float[] {
				(float)Math.atan(fovX / 2), 0, 0, 0,
				0, (float)Math.atan(fovY / 2), 0, 0,
				0, 0, -(farZ + nearZ) / (farZ - nearZ), -2 * (nearZ * farZ) / (farZ - nearZ),
				0, 0, -1, 0
		}).tra();
	}
}
