package com.tvja.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.tvja.utils.AssetUtils;
import com.tvja.utils.MathUtils;

import java.util.Optional;

public class Light extends ViewWorldObject {
    static float nearZ = 0.01f;
    static float farZ = 100;
    protected Vector3 color;
    private Float angle;
    public ModelInstance model;

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

    //TODO: Change projection matrix parameters for all 3 constructors
    
    public static Light newDirectional(Vector3 orientation, Vector3 color) {
        Light l = new Light(new Vector3(), orientation, color, null);
        Vector3 dir = l.getDirection().nor();
        l.setPosition(dir.scl(10));
        l.projection = MathUtils.genOrthogonalProjection(nearZ, farZ, 20, 20);
        Texture tex = AssetUtils.textureFromFile("models/plain.jpg");
        Mesh arrow = AssetUtils.meshFromFile("models/arrow.obj");
        l.model = new ModelInstance(arrow, tex);
        l.model.setPosition(l.getPosition());
        l.model.setOrientation(l.getOrientation());
        return l;
    }

    public static Light newPointSideLight(Vector3 position, Vector3 orientation) {
        Light l = new Light(position, orientation, new Vector3(1,1,1), null);
        l.projection = MathUtils.genPerspectiveProjection(nearZ, farZ, 45, 45);
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
        Texture tex = AssetUtils.textureFromFile("models/plain.jpg");
        Mesh arrow = AssetUtils.meshFromFile("models/arrow.obj");
        l.model = new ModelInstance(arrow, tex);
        l.model.setPosition(l.getPosition());
        l.model.setOrientation(l.getOrientation());
        return l;
    }

    public Vector3 getColor() {
        return color;
    }

    public Optional<Float> getAngle() {
        return Optional.ofNullable(angle);
    }
}
