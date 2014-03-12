package com.rs3e.network.protocol.login;


public class LoginLobbyPayload {
	
	private byte[] xteaBlock;
	private int[] xteaKeys;
	private String password;
	
	public LoginLobbyPayload (String password, int[] xteaKeys, byte[] payload) {
		this.password = password;
		this.xteaKeys = xteaKeys;
		this.xteaBlock = payload;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int[] getXteaKeys() {
		return this.xteaKeys;
	}
	
	public byte[] getXteaBlock() {
		return this.xteaBlock;
	}
}
