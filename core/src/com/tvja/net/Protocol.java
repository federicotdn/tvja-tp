package com.tvja.net;

public class Protocol {
	private static Protocol instance;
	public static final Integer SERVER_PORT = 3003;
	public static final Integer CLIENT_PORT = 3004;

	public enum Code {
		NEW_CLIENT, ACK_CLIENT, NET_OBJECTS;
	}
}
