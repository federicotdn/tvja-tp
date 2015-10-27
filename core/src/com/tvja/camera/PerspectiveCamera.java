package com.tvja.camera;

import com.badlogic.gdx.math.Vector3;
import com.tvja.render.ViewWorldObject;
import com.tvja.utils.MathUtils;

public class PerspectiveCamera extends ViewWorldObject {
	private static final float DEFAULT_FOVX = 60.0f;
	private static final float DEFAULT_FOVY = 60.0f;
	private static final float DEFAULT_NEARZ = 0.1f;
	private static final float DEFAULT_FARZ = 100.0f;

	public PerspectiveCamera(float nearZ, float farZ, float fovX, float fovY) {
		super(nearZ, farZ);
		projection = MathUtils.genPerspectiveProjection(nearZ, farZ, fovX, fovY);
	}

	public PerspectiveCamera() {
		this(DEFAULT_NEARZ, DEFAULT_FARZ, DEFAULT_FOVX, DEFAULT_FOVY);
		this.setOrientation(new Vector3(-(float) Math.PI/4, -(float) Math.PI, 0));
		this.setPosition(new Vector3(0,2, -2));
	}
}
