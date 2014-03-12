package com.rs3e.network.protocol.messages;

/**
 * @author Belthazar - 731 Updates
 * @author Sir Tom - 530 Base
 */
public class LoginMessage {
	
	public static final int INVALID_UN_PWD = 3;
	public static final int ACCOUNT_DISABLED = 4;
	public static final int NOT_LOGGED_OUT = 5;
	public static final int GAME_UPDATED = 6;
	public static final int WORLD_FULL = 7;
	public static final int LOGIN_SERVER_DOWN = 8;
	public static final int LOGIN_LIMIT_EXCEDED = 9;
	public static final int BAD_SESSION = 10;
	public static final int PASSWORD_COMMON = 11;
	public static final int MEMBERS_WORLD = 12;
	public static final int LOGIN_INCOMPLETE = 13;
	public static final int SERVER_UPDATING = 14;
	public static final int UNEXPECTED_RESPONSE = 15;
	public static final int TOO_MANY_INCORRECT_LOGINS = 16;
	public static final int IN_MEMBERS_AREA = 17;
	public static final int ACCOUNT_LOCKED = 18;
	public static final int FULLSCREEN_P2P = 19;
	public static final int INVALID_LOGIN_SERVER = 20;
	public static final int PROFILE_TRANSFERING = 21;
	public static final int BAD_LOGIN_PACKET = 22;
	public static final int NO_LOGIN_RESPONSE = 23;
	public static final int ERROR_PROFILE_LOAD = 24;
	public static final int UNEXPECTED_LOGIN_RESPONSE = 25;
	public static final int IP_BLOCKED = 26;
	public static final int JAG_UNAUTHORISED = 50;
	public static final int EMAIL_NEEDS_VALIDATING = 51;
	
	private int returnCode;
	
	public LoginMessage (int code) {
		this.returnCode = code;
	}
	
	/**
	 * Gets the return code.
	 * @return the returnCode
	 */
	public int getReturnCode() {
		return returnCode;
	}

}
