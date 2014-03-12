package com.rs3e.network.protocol.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.net.ProtocolException;
import java.security.SecureRandom;

import com.rs3e.Constants;
import com.rs3e.network.protocol.messages.LoginResponse;
import com.rs3e.network.protocol.messages.LoginResponse;
import com.rs3e.utility.ByteBufUtils;


/**
 * @author Belthazar - 731 Updates
 * @author Sir Tom - 530 Base
 */
public class LoginDecoder extends ByteToMessageDecoder<Object> {

	/**
	 * An enumeration used for storing the possible states of login.
	 */
	public enum LoginState { DECODE_HEADER, CONNECTION_TYPE, CLIENT_DETAILS, PAYLOAD };
	
	/**
	 * The default login state.
	 */
	private LoginState state = LoginState.CONNECTION_TYPE;
	
	private int loginSize;
	private LoginType currentLoginType;
	
	/*
	 * (non-Javadoc)
	 * @see com.rse.network.StatedByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		//System.out.println("Received login request...");
		switch (getState()) {
			case DECODE_HEADER:
				return decodeHeader(ctx, buf);
			case CONNECTION_TYPE:
				return decodeConnectionType(buf);
				//return decodePayload(ctx, buf);
			case CLIENT_DETAILS:
				return decodeClientDetails(buf);
			case PAYLOAD:
				decodeLobbyPayload(ctx, buf);
				break;
		}
		return null;
	}

	/**
	 * Decodes the header of login.
	 * @param ctx The channel handler context.
	 * @param buf The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	private Object decodeHeader(ChannelHandlerContext ctx, ByteBuf buf) {
		if (buf.readable()) {
			
			@SuppressWarnings("unused")
			//long clientHash = buf.readUnsignedByte();
			int secureKey = new SecureRandom().nextInt();

			ByteBuf unpooled = Unpooled.buffer();
			unpooled.writeByte(0);
			//unpooled.writeLong(secureKey);
			ctx.channel().write(unpooled);

			setState(LoginState.CONNECTION_TYPE);
		}
		return null;
	}
	
	/**
	 * Decodes the payload of login.
	 * @param ctx The channel handler context.
	 * @param buf The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	/*private Object decodePayload(ChannelHandlerContext ctx, ByteBuf buf) {
		if(buf.readable()) {
			
			int loginType = buf.readByte();
			System.out.println("Login Type: " + loginType);
		}
		return null;
	}*/
	
	private Object decodePayload(ChannelHandlerContext context, ByteBuf buffer) {
		byte[] payload = new byte[buffer.readableBytes()];
		buffer.readBytes(payload);
		return new LoginPayload(currentLoginType, payload);
		/*int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			throw new ProtocolException("Invalid secure buffer size.");
		}

		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ByteBuf secureBuffer = Unpooled.wrappedBuffer(new BigInteger(secureBytes).modPow(Constants.JS5PrivateKey, Constants.JS5ModulusKey).toByteArray());
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			throw new ProtocolException("Invalid block opcode.");
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {
			throw new ProtocolException("Invalid login virtual hash.");
		}

		String password = ByteBufUtils.readString(secureBuffer);

		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		buffer.readBytes(xteaBlock);
		
		return new LoginLobbyPayload(password, xteaKey, xteaBlock);*/		
	}
	
	private Object decodeClientDetails(ByteBuf buffer) throws ProtocolException {
		if (buffer.readableBytes() < loginSize) {
			throw new ProtocolException("Invalid login size.");
		}

		int version = buffer.readInt();
		int subVersion = buffer.readInt();

		if (version != Constants.ServerRevision && subVersion != Constants.ServerSubRevision) {
			return new LoginResponse(LoginResponse.GAME_UPDATED);
			//throw new ProtocolException("Invalid client version/sub-version.");
		}

		/*if (currentLoginType.equals(LoginTypes.GAME)) {
			buffer.readByte();
		}*/
		
		byte[] payload = new byte[loginSize-8];
		buffer.readBytes(payload);
		return new LoginPayload(currentLoginType, payload);
		//state = currentLoginType.equals(LoginTypes.LOBBY) ? LoginState.LOBBY_PAYLOAD : LoginState.GAME_PAYLOAD;
		//return null;
	}
	
	private Object decodeConnectionType(ByteBuf buffer) throws ProtocolException {
		int loginType = buffer.readUnsignedByte();
		if (loginType != 16 && loginType != 18 && loginType != 19) {
			throw new ProtocolException("Invalid login opcode: " + loginType);
		}

		currentLoginType = loginType == 19 ? LoginType.LOBBY : LoginType.GAME;
		loginSize = buffer.readShort() & 0xFFFF;

		state = LoginState.CLIENT_DETAILS;
		return null;
	}

	/**
	 * Gets the state.
	 * @return the state
	 */
	public LoginState getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * @param state the state to set
	 */
	public void setState(LoginState state) {
		this.state = state;
	}
}
