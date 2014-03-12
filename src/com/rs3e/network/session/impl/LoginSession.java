package com.rs3e.network.session.impl;

import io.netty.channel.ChannelHandlerContext;

import com.rs3e.network.session.Session;
import com.rsps.Constants;
import com.rsps.game.player.Player;
import com.rsps.util.Base37Utils;
import com.rsps.util.GeneralUtils;
import com.rsps.util.SerializableFilesManager;
import com.rsps.util.XTEA;

/**
 * @author Belthazar - 731 Updates
 * @author Sir Tom - 530 Base
 */
public class LoginSession extends Session {

	/**
	 * Constructs a new {@link LoginSession} instance.
	 * @param context The context of the channel.
	 */
	public LoginSession(ChannelHandlerContext context) {
		super(context);
		//System.out.println("Initalised login session...");
	}

	/* (non-Javadoc)
	 * @see com.rse.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {

	}

	/* (non-Javadoc)
	 * @see com.rse.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		//System.out.println("Login message method called...");
		if (obj instanceof LoginLobbyPayload) {
			LoginLobbyPayload lobbyData = (LoginLobbyPayload) obj;
			byte[] xteaBlock = lobbyData.getXteaBlock();
			XTEA xtea = new XTEA(lobbyData.getXteaKeys());			
			xtea.decrypt(xteaBlock, 0, xteaBlock.length);

			InputStream xteaBuffer = new InputStream(xteaBlock);

			boolean decodeAsString = xteaBuffer.readByte() == 1;
			String username = decodeAsString ? xteaBuffer.readString() : Base37Utils.decodeBase37(xteaBuffer.readLong());

			@SuppressWarnings("unused")
			int gameType = xteaBuffer.readUnsignedByte();
			@SuppressWarnings("unused")
			int languageID = xteaBuffer.readUnsignedByte();
			
			@SuppressWarnings("unused")
			int displayMode = xteaBuffer.readByte();				
			@SuppressWarnings("unused")
			int screenWidth = xteaBuffer.readUnsignedShort();//Client screen width
			@SuppressWarnings("unused")
			int screenHeight = xteaBuffer.readUnsignedShort();//Client screen height
			@SuppressWarnings("unused")
			int anUnknownByte = xteaBuffer.readByte();
			
			byte[] randomData = new byte[24];
			for (int i = 0; i < randomData.length; i++) {
				randomData[i] = (byte) (xteaBuffer.readByte() & 0xFF);
			}
			
			@SuppressWarnings("unused")
			String clientSettings = xteaBuffer.readString();
			
			int indexFiles  = xteaBuffer.readByte() & 0xff;

			int[] crcValues = new int[indexFiles];

			for (int i = 0; i < crcValues.length; i++) {
				crcValues[i] = xteaBuffer.readInt(); 
			}

			int length = xteaBuffer.readUnsignedByte();

			byte[] machineData = new byte[length];
			for (int data = 0; data < machineData.length; data++) {
				machineData[data] = (byte) xteaBuffer.readUnsignedByte();
			}

			xteaBuffer.readInt();//Packet receive count
			xteaBuffer.readString();//Some param string (empty)
			xteaBuffer.readInt();//Another param (0) 
			xteaBuffer.readInt();//Yet another param (2036537831)

			String serverToken = xteaBuffer.readString();
			if (!serverToken.equals(Constants.SERVER_TOKEN)) {
				channel.write(new LoginMessage(LoginMessage.BAD_SESSION));
				return;
			}
			
			xteaBuffer.readByte();//Final param (2424)

			if (GeneralUtils.invalidAccountName(username)) {
				//session.getLoginPackets().sendClientPacket(3);//Invalid username or password
				channel.write(new LoginMessage(LoginMessage.INVALID_UN_PWD));
				return;
			}
			/*
			if (World.getPlayers().size() >= Settings.PLAYERS_LIMIT - 10) {
				session.getLoginPackets().sendClientPacket(7);//World full
				return;
			}
			if (World.containsPlayer(username) || World.containsLobbyPlayer(username)) {
				session.getLoginPackets().sendClientPacket(5);//Account not logged out
				return;
			}
			if (AntiFlood.getSessionsIP(session.getIP()) > 3) {
				session.getLoginPackets().sendClientPacket(9);//Login limit exceeded
				return;
			}*/
			Player player;// = new Player(new PlayerDefinition(username, password));
			if (!SerializableFilesManager.containsPlayer(username)) {
				player = new Player(lobbyData.getPassword());//Create new player
			} else {
				player = SerializableFilesManager.loadPlayer(username);
				if (player == null) {
					//session.getLoginPackets().sendClientPacket(20);//Invalid login server
					channel.write(new LoginMessage(LoginMessage.INVALID_LOGIN_SERVER));
					return;
				}
				/*if (!SerializableFilesManager.createBackup(username)) {
					//session.getLoginPackets().sendClientPacket(20);//Invalid login server
					//return;
				}*/
				if (!player.isCorrectPassword(lobbyData.getPassword())) {
					//session.getLoginPackets().sendClientPacket(3);
					channel.write(new LoginMessage(LoginMessage.INVALID_UN_PWD));
					return;
				}
			}// || player.getBanned() > Utils.currentTimeMillis()
			if (player.isPermBanned()) {
				//session.getLoginPackets().sendClientPacket(4);//Account disabled
				channel.write(new LoginMessage(LoginMessage.ACCOUNT_DISABLED));
				return;
			}//24 = account does not exist
			player.lobbyInit(context.channel(), username);
			
			/*int returnCode = 2;
			if (FileManager.contains(username)) {
				player = (Player) FileManager.load(username);
				if (player == null) {
					returnCode = 24;
				} else if (!password.equals(player.getDefinition().getPassword()) && !Constants.isOwnerIP(channel.getRemoteAddress().toString().split(":")[0].replace("/", ""))) {
					returnCode = 3;
				}
			} else {
				player = new Player(new PlayerDefinition(username, password));
			}

			player.init(channel, currentLoginType);
			World.getWorld().register(player, returnCode, currentLoginType);

			UpstreamChannelHandler handler = (UpstreamChannelHandler) channel.getPipeline().get("upHandler");
			handler.setPlayer(player);

			context.getChannel().setAttachment(player);

			channel.getPipeline().replace("decoder", "decoder", new InBufferDecoder());*/
		}
	}
}
