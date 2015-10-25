package com.tvja.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hobbit on 10/25/15.
 */
public class FrameBufferPool {
    private static FrameBufferPool instance;
    private List<FrameBuffer> pool;
    private int increaseFactor;

    private FrameBufferPool(int initialCapacity, int increaseFactor) {
        this.increaseFactor = increaseFactor;
        pool = new LinkedList<>();
        addFrameBuffers(initialCapacity);
    }

    public FrameBuffer retrieve() {
        if (pool.size() ==0) {
            addFrameBuffers(increaseFactor);
        }
        FrameBuffer fb = pool.get(0);
        pool.remove(0);

        return fb;
    }

    public void returnFB(FrameBuffer fb) {
        if (fb == null) {
            throw new IllegalArgumentException("FrameBuffer cannot be null");
        }
        pool.add(fb);
    }

    public static FrameBufferPool getInstance() {
        synchronized (instance) {
            if (instance == null) {
                instance = new FrameBufferPool(10, 5);
            }
            return instance;
        }
    }

    private void addFrameBuffers(int amount) {
        for (int i =0; i < amount; i++) {
            pool.add(new FrameBuffer(Pixmap.Format.RGB888, 2048, 2048, true));
        }
    }

}
