package com.tvja.net;

import com.tvja.render.SpatialObject;
import com.tvja.render.WorldObject;

import java.io.Serializable;

public class NetworkObject extends SpatialObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int IDGen = 0;

    private int ID;

    public NetworkObject() {
    }
    
    public void genID() {
    	this.ID = IDGen++;
    }
    
    public int getID() {
    	return ID;
    }
}
