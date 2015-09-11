package com.tvja.utils;

import com.badlogic.gdx.math.Vector3;

public class VecUtils {

    public static float[] toVec4f(Vector3 v) {
        return new float[] {v.x, v.y, v.z, 1};
    }
    
    public static Vector3 toDirection(Vector3 orientation) {
    	Vector3 dir = new Vector3(0, 0, 1);
    	dir.rotateRad(new Vector3(1, 0, 0), orientation.x);
    	dir.rotateRad(new Vector3(0, 1, 0), orientation.y);
    	dir.rotateRad(new Vector3(0, 0, 1), orientation.z);
    	return dir;
    }
}
