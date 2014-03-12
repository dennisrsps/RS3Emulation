package com.rs3e.network.protocol.messages;

public class LoginResponse {
	
	public enum Type {
		LOBBY(2), GAME(2);
		
		public final int returnCode;
		
		Type (int code) {
			this.returnCode = code;
		}
	}

	/**
	 * The return code, rights, and index of the response.
	 */
	private int payloadSize;
	
	private Type returnType;
	
	private byte[] payload;
	
	/**
	 * Constructs a new {@code LoginResponse} instance.
	 * @param returnCode The return code.
	 * @param rights The rights.
	 * @param index The index.
	 * @param displayName The displayname.
	 */
	public LoginResponse(Type returnType, byte[] payload, int payloadSize) {
		this.returnType = returnType;
		this.payload = payload;
		this.payloadSize = payloadSize;
	}

	/**
	 * Gets the return code.
	 * @return the returnCode
	 */
	public Type getReturnType() {
		return returnType;
	}

	/**
	 * Gets the payload for the response packet.
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Gets the payload size.
	 * @return the payload size
	 */
	public int getPayloadSize() {
		return payloadSize;
	}
}
