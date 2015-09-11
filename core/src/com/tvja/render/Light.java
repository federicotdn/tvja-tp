package com.tvja.render;

import com.badlogic.gdx.math.Vector3;

import java.util.Optional;

public class Light {
	
    protected Vector3 position;
    protected Vector3 direction;
    protected Vector3 color;
    private Float angle;

    public static Light newDirectional(Vector3 direction, Vector3 color) {
    	return new Light(null, direction, color, null);
    }
    
    public static Light newPoint(Vector3 position, Vector3 color) {
    	return new Light(position, null, color, null);
    }
    
    public static Light newSpot(Vector3 position, Vector3 direction, Vector3 color, Float angle) {
    	return new Light(position, direction, color, angle);
    }
    
    public Light(Vector3 position, Vector3 direction, Vector3 color,  Float angle) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        
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
    
    public void setPosition(Vector3 position) {
    	this.position = position;
    }

    public Optional<Vector3> getDirection() {
        return Optional.ofNullable(direction);
    }
    
    public void setDirection(Vector3 direction) {
    	this.direction = direction;
    }

    public Vector3 getColor() {
        return color;
    }

    public Optional<Float> getAngle() {
        return Optional.ofNullable(angle);
    }
}
