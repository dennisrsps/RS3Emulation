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
package com.rs3e.network.protocol.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;

import com.rs3e.network.protocol.js5.UpdateDecoder;
import com.rs3e.network.protocol.js5.UpdateEncoder;
import com.rs3e.network.protocol.js5.UpdateStatusEncoder;
import com.rs3e.network.protocol.js5.XorEncoder;
import com.rs3e.network.protocol.messages.HandshakeMessage;
import com.rs3e.network.protocol.worldlist.WorldListDecoder;
import com.rs3e.network.protocol.worldlist.WorldListEncoder;

/**
 * An {@link ChannelInboundByteHandlerAdapter} that is used to handle the
 * handshake procedure of the server->client connection.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class HandshakeDecoder extends ChannelInboundByteHandlerAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundByteHandlerAdapter#inboundBufferUpdated
	 * (io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void inboundBufferUpdated(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		if (!in.readable())
			return;
		ctx.pipeline().remove(HandshakeDecoder.class);

		int incomingOpcode = in.readByte() & 0xFF;
		HandshakeState handshakeState = HandshakeState.forId(incomingOpcode);

		if (handshakeState == null) {
			return;
		}
		switch (handshakeState) {
		case HANDSHAKE_UPDATE:
			ctx.pipeline().addFirst(new UpdateEncoder(),
					new UpdateStatusEncoder(), new XorEncoder(),
					new UpdateDecoder());
			break;
		case HANDSHAKE_WORLD_LIST:
			ctx.pipeline().addFirst(new WorldListEncoder(),
					new WorldListDecoder());
			break;
		case HANDSHAKE_LOGIN:
			// ctx.pipeline().addFirst(new LoginEncoder(), new LoginDecoder(),
			// new LoginMessageEncoder());
			// ctx.write(new LoginMessage(0));
			break;
		default:
			break;
		}

		ctx.nextInboundMessageBuffer()
				.add(new HandshakeMessage(handshakeState));
		ctx.fireInboundBufferUpdated();

		if (in.readable()) {
			ChannelHandlerContext head = ctx.pipeline().firstContext();
			head.nextInboundByteBuffer().writeBytes(
					in.readBytes(in.readableBytes()));
			head.fireInboundBufferUpdated();
		}
	}
}