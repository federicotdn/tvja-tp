package com.tvja.camera;

import com.tvja.render.ViewWorldObject;
import com.tvja.utils.MathUtils;

public class OrthogonalCamera extends ViewWorldObject {

	private static final float DEFAULT_WIDTH = 5.0f;
	private static final float DEFAULT_HEIGHT = 5.0f;
	private static final float DEFAULT_NEARZ = 0.1f;
	private static final float DEFAULT_FARZ = 100.0f;

	public OrthogonalCamera(float nearZ, float farZ, float height, float width) {
		projection = MathUtils.genOrthogonalProjection(nearZ, farZ, height, width);
	}

	public OrthogonalCamera() {
		this(DEFAULT_NEARZ, DEFAULT_FARZ, DEFAULT_HEIGHT, DEFAULT_WIDTH);
	}
}
