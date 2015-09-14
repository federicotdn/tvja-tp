package com.tvja.render;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Hobbit on 9/14/15.
 */
public abstract class ViewWorldObject<T extends WorldObject<T>> extends WorldObject<T> {
    protected Matrix4 projection;

    public Matrix4 getViewProjection() {
        return projection.cpy().mul(getTRS().inv());
    }

    public ViewWorldObject(Vector3 pos, Vector3 ori, Vector3 scale) {
        super(pos, ori, scale);
    }

    public ViewWorldObject(Vector3 pos, Vector3 ori) {
        super(pos, ori);
    }

    public ViewWorldObject() {
        super();
    }

}
