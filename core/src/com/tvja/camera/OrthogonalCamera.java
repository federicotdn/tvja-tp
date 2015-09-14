package com.tvja.camera;

import com.tvja.utils.MathUtils;

public class OrthogonalCamera extends Camera {

	public OrthogonalCamera(float nearZ, float farZ, float height, float width) {
		projection = MathUtils.genOrthogonalProjection(nearZ, farZ, height, width);
	}
}
