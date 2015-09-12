package com.tvja.camera;

import com.badlogic.gdx.math.Matrix4;

public class OrthogonalCamera extends Camera {

	public OrthogonalCamera(float nearZ, float farZ, float height, float width) {
		projection = createOrthogonalProjection(nearZ, farZ, height, width);
	}

	private Matrix4 createOrthogonalProjection(float nearZ, float farZ, float height, float width) {
		return new Matrix4(new float[] { 1 / width, 0, 0, 0, 
				                           0, 1 / height, 0, 0, 
				                           0, 0, -2 / (farZ - nearZ), -(nearZ + farZ) / (farZ - nearZ), 
				                           0, 0, 0, 1 }).tra();
	}
}
