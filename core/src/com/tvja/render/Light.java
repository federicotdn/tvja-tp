package com.tvja.render;

import com.badlogic.gdx.math.Vector3;

import java.util.Optional;

/**
 * Created by Hobbit on 9/4/15.
 */
public class Light {
	private static final float AMBIENT_MULT = 0.05f;
	
    protected Vector3 position;
    protected Vector3 direction;
    protected Vector3 color;
    protected Vector3 ambientColor;
    private Float angle;

    public static Light newDirectional(Vector3 direction, Vector3 color) {
    	return new Light(null, direction, color, new Vector3(color).scl(AMBIENT_MULT), null);
    }
    
    public static Light newPoint(Vector3 position, Vector3 color) {
    	return new Light(position, null, color, new Vector3(color).scl(AMBIENT_MULT), null);
    }
    
    public static Light newSpot(Vector3 position, Vector3 direction, Vector3 color, Float angle) {
    	return new Light(position, direction, color, new Vector3(color).scl(AMBIENT_MULT), angle);
    }
    
    public Light(Vector3 position, Vector3 direction, Vector3 color, Vector3 ambientColor, Float angle) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.ambientColor = ambientColor;
        if (angle != null) {
            if (Math.abs(angle) > Math.PI/2) {
                angle = (float) Math.PI/2;
            }
            this.angle = (float)Math.cos(angle);
        }
    }

    public Optional<Vector3> getPosition() {
        return Optional.ofNullable(position);
    }

    public  Optional<Vector3> getDirection() {
        return Optional.ofNullable(direction);
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector3 getAmbientColor() {
        return ambientColor;
    }

    public Optional<Float> getAngle() {
        return Optional.ofNullable(angle);
    }
}
