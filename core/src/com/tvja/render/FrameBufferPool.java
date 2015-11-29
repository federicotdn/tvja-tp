package com.tvja.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hobbit on 11/29/15.
 */
public class FrameBufferPool {
    private static FrameBufferPool instance;
    private static final int POOL_SIZE = 5;
    private Map<Integer, List<FrameBuffer>> map;
    private int growSize;

    private FrameBufferPool() {
        map = new HashMap<>();
        growSize = POOL_SIZE;
    }

    public static FrameBufferPool getInstance() {
        if (instance == null) {
            instance = new FrameBufferPool();
        }
        return  instance;
    }

    public FrameBuffer getFrameBuffer(int size) {
        if (!map.containsKey(size)) {
            List<FrameBuffer> l = new LinkedList<>();
            map.put(size, l);
        }
        List<FrameBuffer> l = map.get(size);
        if (l.size() == 0) {
            for (int i =0; i < growSize; i++) {
                l.add(new FrameBuffer(Pixmap.Format.RGB888, size, size, true));
            }
        }

        FrameBuffer fb = l.get(0);

        l.remove(0);

        return fb;
    }

    public void returnFrameBuffer(FrameBuffer fb) {
        if (!map.containsKey(fb.getWidth())) {
            throw new IllegalArgumentException("Invalid size");
        }
        map.get(fb.getWidth()).add(fb);
    }
}
