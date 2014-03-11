package com.rs3e.network.protocol.messages;

public final class WorldHandshakeMessage {

	private final int sessionId;

	public WorldHandshakeMessage(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getSessionId() {
		return sessionId;
	}

}
