package com.tvja.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MathUtils {

    public static float[] toVec4fPoint(Vector3 v) {
        return new float[] {v.x, v.y, v.z, 1};
    }

	public static float[] toVec4fDirection(Vector3 v) {
		return new float[] {v.x, v.y, v.z, 0};
	}
    
    public static Vector3 toDirection(Vector3 orientation) {
    	Vector3 dir = new Vector3(0, 0, 1);
    	dir.rotateRad(new Vector3(1, 0, 0), orientation.x);
    	dir.rotateRad(new Vector3(0, 1, 0), orientation.y);
    	dir.rotateRad(new Vector3(0, 0, 1), orientation.z);
    	return dir;
    }
    
	public static Matrix4 genPerspectiveProjection(float nearZ, float farZ, float fovX, float fovY) {
		return new Matrix4(new float[] {
				(float)Math.atan(fovX / 2), 0, 0, 0,
				0, (float)Math.atan(fovY / 2), 0, 0,
				0, 0, -(farZ + nearZ) / (farZ - nearZ), -2 * (nearZ * farZ) / (farZ - nearZ),
				0, 0, -1, 0
		}).tra();
	}
	
	public static Matrix4 genOrthogonalProjection(float nearZ, float farZ, float height, float width) {
		return new Matrix4(new float[] { 
				1 / width, 0, 0, 0, 
				0, 1 / height, 0, 0, 
				0, 0, -2 / (farZ - nearZ), -(nearZ + farZ) / (farZ - nearZ), 
				0, 0, 0, 1
		}).tra();
	}
}