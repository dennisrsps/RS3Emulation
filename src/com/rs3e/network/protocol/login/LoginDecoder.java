/*
 * This file is part of Ieldor.
 *
 * Ieldor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ieldor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ieldor.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rs3e.network.protocol.login;

import java.security.SecureRandom;

import com.rs3e.network.protocol.messages.LoginHandshakeMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * An {@link ByteToMessageDecoder} that handles the login procedure.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginDecoder extends ByteToMessageDecoder<LoginHandshakeMessage> {

	/**
	 * An enumeration used for storing the possible states of login.
	 *
	 * @author Thomas Le Godais <thomaslegodais@live.com>
	 *
	 */
	public enum LoginState { DECODE_HEADER, DECODE_PAYLOAD };
	
	/**
	 * The default login state.
	 */
	private LoginState state = LoginState.DECODE_HEADER;
	
	/*
	 * (non-Javadoc)
	 * @see com.rs3e.network.StatedByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public LoginHandshakeMessage decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		switch (getState()) {
		case DECODE_HEADER:
			return decodeHeader(ctx, buf);
		case DECODE_PAYLOAD:
			return decodePayload(ctx, buf);
		}
		return null;
	}

	/**
	 * Decodes the header of login.
	 * @param ctx The channel handler context.
	 * @param buf The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	private LoginHandshakeMessage decodeHeader(ChannelHandlerContext ctx, ByteBuf buf) {
		if (buf.readable()) {
			
			@SuppressWarnings("unused")
			long clientHash = buf.readUnsignedByte();
			int secureKey = new SecureRandom().nextInt();

			ByteBuf unpooled = Unpooled.buffer();
			unpooled.writeByte(0);
			unpooled.writeLong(secureKey);
			ctx.channel().write(unpooled);

			setState(LoginState.DECODE_PAYLOAD);
		}
		return null;
	}
	
	/**
	 * Decodes the payload of login.
	 * @param ctx The channel handler context.
	 * @param buf The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	private LoginHandshakeMessage decodePayload(ChannelHandlerContext ctx, ByteBuf buf) {
		if(buf.readable()) {
			
			int loginType = buf.readByte();
			System.out.println("Login Type: " + loginType);
		}
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
