package com.tvja.utils;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Hobbit on 9/4/15.
 */
public class VecUtils {

    public static float[] toVec4f(Vector3 v) {
        return new float[] {v.x, v.y, v.z, 1};
    }
}
