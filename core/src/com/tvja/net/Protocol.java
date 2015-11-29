package com.tvja.net;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
	private static Protocol instance;
	public static final Integer SERVER_PORT = 3003;
	public static final Integer CLIENT_PORT = 3004;
	private Map<Byte, Code> protocolCodes;
	
	private Protocol() {
		protocolCodes = new HashMap<Byte, Code>();
		for (Code c : Code.values()) {
			protocolCodes.put(c.getHeader(), c);
		}
	}
	
	public static Code parseHeader(byte header) {
		if (instance == null) {
			instance = new Protocol();
		}
		
		Code c = instance.protocolCodes.get(header);
		if (c == null) {
			throw new RuntimeException("Invalid package header.");
		}
		
		return c;
	}
	
	public enum Code {
		NEW_CLIENT((byte) 0x61), ACK_CLIENT((byte) 0x62);

		private byte header;

		private Code(byte header) {
			this.header = header;
		}

		public byte getHeader() {
			return header;
		}
	}
}
