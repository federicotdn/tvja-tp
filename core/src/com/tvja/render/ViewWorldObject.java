package com.tvja.render;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Hobbit on 9/14/15.
 */
public abstract class ViewWorldObject extends WorldObject {
    protected Matrix4 projection;
    private float nearZ;
    private float farZ;

    public Matrix4 getViewProjection() {
        return projection.cpy().mul(getTRS().inv());
    }

    public ViewWorldObject(float nearZ, float farZ, Vector3 pos, Vector3 ori, Vector3 scale) {
        super(pos, ori, scale);
        this.nearZ = nearZ;
        this.farZ = farZ;
    }

    public ViewWorldObject(float nearZ, float farZ, Vector3 pos, Vector3 ori) {
        super(pos, ori);
        this.nearZ = nearZ;
        this.farZ = farZ;
    }

    public ViewWorldObject(float nearZ, float farZ) {
       this.nearZ = nearZ;
       this.farZ = farZ;
    }
    
    public float getNearZ() {
    	return nearZ;
    }
    
    public float getFarZ() {
    	return farZ;
    }
}
