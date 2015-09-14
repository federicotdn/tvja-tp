package com.tvja.render;

import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

import java.util.Optional;

public class Light extends ViewWorldObject<Light> {
    protected Vector3 color;
    private Float angle;

    public static Light newDirectional(Vector3 orientation, Vector3 color) {
    	return new Light(new Vector3(), orientation, color, null);
    }
    
    public static Light newPoint(Vector3 position, Vector3 color) {
    	return new Light(position, new Vector3(), color, null);
    }
    
    public static Light newSpot(Vector3 position, Vector3 orientation, Vector3 color, Float angle) {
    	return new Light(position, orientation, color, angle);
    }
    
    public Light(Vector3 position, Vector3 orientation, Vector3 color, Float angle) {
    	super(position, orientation);
        this.color = color;
        
        if (angle != null) {
            if (Math.abs(angle) > Math.PI/2) {
                angle = (float) Math.PI/2;
            }
            this.angle = (float)Math.cos(angle);
        }
        
        // change so that appropiate projection is created
        projection = MathUtils.genPerspectiveProjection(0.01f, 100, 60, 60);
    }

    public Vector3 getColor() {
        return color;
    }

    public Optional<Float> getAngle() {
        return Optional.ofNullable(angle);
    }

	@Override
	protected Light getThis() {
		return this;
	}
}
