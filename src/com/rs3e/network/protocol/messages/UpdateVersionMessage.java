package com.rs3e.network.protocol.messages;

public final class UpdateVersionMessage {

	private final int version;
	private final int subVersion;
	private final String key;

	public UpdateVersionMessage(int version, int subVersion, String key) {
		this.version = version;
		this.subVersion = subVersion;
		this.key = key;
	}

	public int getVersion() {
		return version;
	}

	public int getSubVersion() {
		return subVersion;
	}

	public String getKey() {
		return key;
	}

}
