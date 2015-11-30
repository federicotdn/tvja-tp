package com.tvja.net;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class Player {
	private InetSocketAddress address;
	private NetworkObject avatar;
	private Integer id;
	private DatagramChannel channel;
	
	public Player(InetSocketAddress address) {
		this.address = address;
		this.id = hashCode();
	}
	
	public DatagramChannel getChannel() {
		return channel;
	}

	public void setChannel(DatagramChannel channel) {
		this.channel = channel;
	}

	public InetSocketAddress getAddress() {
		return address;
	}
	
	public void setAvatar(NetworkObject avatar) {
		this.avatar = avatar;
	}
	
	public NetworkObject getAvatar() { 
		return avatar;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		Player other = (Player) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}
}
