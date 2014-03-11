package com.rs3e.network.protocol.messages;

public final class UpdateEncryptionMessage {

	private final int key;

	public UpdateEncryptionMessage(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}
