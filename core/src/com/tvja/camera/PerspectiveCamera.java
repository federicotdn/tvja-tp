package com.tvja.camera;

import com.tvja.utils.MathUtils;

public class PerspectiveCamera extends Camera {
	public PerspectiveCamera(float nearZ, float farZ, float fovX, float fovY) {
		projection = MathUtils.genPerspectiveProjection(nearZ, farZ, fovX, fovY);
	}
}
