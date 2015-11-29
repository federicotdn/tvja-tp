package com.tvja.net;

import com.badlogic.gdx.math.Vector3;
import com.tvja.render.WorldObject;

import java.io.Serializable;

/**
 * Created by Hobbit on 11/29/15.
 */
public class NetworkObject implements Serializable {
    private Vector3 position;
    private Vector3 orientation;
    private Vector3 scaled;

    public NetworkObject(WorldObject wo) {
        this.position = wo.getPosition();
        this.orientation = wo.getOrientation();
        this.scaled = wo.getScale();
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getOrientation() {
        return orientation;
    }

    public void setOrientation(Vector3 orientation) {
        this.orientation = orientation;
    }

    public Vector3 getScaled() {
        return scaled;
    }

    public void setScaled(Vector3 scaled) {
        this.scaled = scaled;
    }
}
