package com.tvja.net;

import com.tvja.render.SpatialObject;
import com.tvja.render.WorldObject;

import java.io.Serializable;

public class NetworkObject extends SpatialObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int IDGen = 0;

    private int ID;

    public NetworkObject() {
    	this.ID = IDGen++;
    }
    
    public NetworkObject(WorldObject wo) {
    	this();
        setPosition(wo.getPosition());
        setOrientation(wo.getOrientation());
        setScale(wo.getScale());
    }
    
    public int getID() {
    	return ID;
    }
}
