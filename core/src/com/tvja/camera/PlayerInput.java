package com.tvja.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by Hobbit on 11/29/15.
 */
public class PlayerInput {
    private boolean w;
    private boolean s;
    private boolean a;
    private boolean d;
    private boolean shift;
    private int deltaX;
    private int deltaY;

    public PlayerInput() {

    }

    public void grabInputs() {
        w = Gdx.input.isKeyPressed(Input.Keys.W);
        s = Gdx.input.isKeyPressed(Input.Keys.S);
        a = Gdx.input.isKeyPressed(Input.Keys.A);
        d = Gdx.input.isKeyPressed(Input.Keys.D);
        shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        deltaX = Gdx.input.getDeltaX();
        deltaY = Gdx.input.getDeltaY();
    }
    
    public boolean hasInputs() {
    	return w || s || a || d || deltaX != 0 || deltaY != 0;
    }

    public boolean isW() {
        return w;
    }

    public void setW(boolean w) {
        this.w = w;
    }

    public boolean isS() {
        return s;
    }

    public void setS(boolean s) {
        this.s = s;
    }

    public boolean isA() {
        return a;
    }

    public void setA(boolean a) {
        this.a = a;
    }

    public boolean isD() {
        return d;
    }

    public void setD(boolean d) {
        this.d = d;
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }
}
