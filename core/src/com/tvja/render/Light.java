package com.tvja.render;

import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.MathUtils;

import java.util.Optional;

public class Light extends ViewWorldObject {
    static float nearZ = 0.01f;
    static float farZ = 100;
    protected Vector3 color;
    private Float angle;

    private Light(Vector3 position, Vector3 orientation, Vector3 color, Float angle) {
        super(nearZ, farZ, position, orientation);

        this.color = color;

        if (angle != null) {
            if (Math.abs(angle) > Math.PI / 2) {
                angle = (float) Math.PI / 2;
            }
            this.angle = (float) Math.cos(angle);
        }
    }

    public static Light newDirectional(Vector3 orientation, Vector3 color) {
        Light l = new Light(new Vector3(), orientation, color, null);
        Vector3 dir = l.getDirection().nor();
        l.setPosition(dir.scl(-10));
        l.projection = MathUtils.genOrthogonalProjection(nearZ, farZ, 5, 5);
        return l;
    }

    public static Light newPoint(Vector3 position, Vector3 color) {
        Light l = new Light(position, new Vector3(), color, null);
        l.projection = MathUtils.genPerspectiveProjection(nearZ, farZ, 60, 60);
        return l;
    }

    public static Light newSpot(Vector3 position, Vector3 orientation, Vector3 color, Float angle) {
        Light l = new Light(position, orientation, color, angle);
        l.projection = MathUtils.genPerspectiveProjection(nearZ, farZ, 60, 60);
        return l;
    }

    public Vector3 getColor() {
        return color;
    }

    public Optional<Float> getAngle() {
        return Optional.ofNullable(angle);
    }
}
