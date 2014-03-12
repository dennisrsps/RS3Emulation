package com.rs3e.network.protocol.login;


public class LoginPayload {
	
	public static enum LoginType {
		LOBBY(19), GAME(16);
		
		public final int opcode;
		
		LoginType (int opcode) {
			this.opcode = opcode;
		}
		
		public static LoginType getType(int opcode) {
			switch (opcode) {
				case 19:
					return LOBBY;
				case 16:
					return GAME;
				default:
					return null;
			}
		}
	}
	
	private byte[] payload;
	private LoginType type;
	
	public LoginPayload (LoginType type, byte[] payload) {
		this.type = type;
		this.payload = payload;
	}
	
	public LoginType getType() {
		return this.type;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
}
