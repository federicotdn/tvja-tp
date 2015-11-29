package com.tvja.net;

import com.tvja.render.SpatialObject;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NetworkObject other = (NetworkObject) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
}
