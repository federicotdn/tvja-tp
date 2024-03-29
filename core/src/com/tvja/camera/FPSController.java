package com.tvja.camera;

import com.badlogic.gdx.math.Vector3;
import com.tvja.render.SpatialObject;

public class FPSController {

    private static final float DEFAULT_SPEED = 0.1f;
    private static final float DEFAULT_SENSIBILITY = 0.01f;

    private float speed;
    private float sensibility;

    public FPSController(float speed, float sensibility) {
        this.speed = speed;
        this.sensibility = sensibility;
    }

    public FPSController() {
        this(DEFAULT_SPEED, DEFAULT_SENSIBILITY);
    }

    private Vector3 getForward(Vector3 orientation) {
        Vector3 forward = new Vector3(0, 0, -1);
        forward.rotateRad(new Vector3(1, 0, 0), orientation.x);
        forward.rotateRad(new Vector3(0, 1, 0), orientation.y);
        return forward.nor();
    }

    private Vector3 getRight(Vector3 orientation) {
        Vector3 right = new Vector3(-1, 0, 0);
        right.rotateRad(new Vector3(1, 0, 0), orientation.x);
        right.rotateRad(new Vector3(0, 1, 0), orientation.y);
        return right.nor();
    }

    public void updatePositionOrientation(SpatialObject so, PlayerInput input) {
        Vector3 ori = so.getOrientation();
        Vector3 pos = so.getPosition();

        updateOrientation(ori, input);
        updatePosition(pos, ori, input);

        so.setPosition(pos);
        so.setOrientation(ori);
    }

    private void updatePosition(Vector3 position, Vector3 orientation, PlayerInput input) {
        float speedAux = speed;

        if (input.isShift()) {
            speedAux *= 5;
        }

        if (input.isW()) {
            position.add(getForward(orientation).scl(speedAux));
        } else if (input.isS()) {
            position.add(getForward(orientation).scl(-speedAux));
        }


        if (input.isD()) {
            position.add(getRight(orientation).scl(-speedAux));
        } else if (input.isA()) {
            position.add(getRight(orientation).scl(speedAux));
        }
    }

    private void correctAngles(Vector3 orientation) {
        while (orientation.y < 0)
            orientation.y += Math.PI * 2;
        while (orientation.y > Math.PI * 2)
            orientation.y -= Math.PI * 2;

        float maxX = (float) (Math.PI / 2);

        if (orientation.x > maxX)
            orientation.x = maxX;
        else if (orientation.x < -maxX)
            orientation.x = -maxX;
    }

    private void updateOrientation(Vector3 orientation, PlayerInput input) {
        int dx = input.getDeltaX();
        int dy = input.getDeltaY();

        orientation.y += ((float) dx) * -sensibility;
        orientation.x += ((float) dy) * -sensibility;

        correctAngles(orientation);
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
}
