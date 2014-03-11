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
package com.rs3e.network.protocol.worldlist;

import com.rs3e.network.protocol.messages.WorldHandshakeMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * An {@link ByteToMessageDecoder} that is used to decode the world list.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class WorldListDecoder extends ByteToMessageDecoder<WorldHandshakeMessage> {

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public WorldHandshakeMessage decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		if (in.readableBytes() < 4)
			return null;

		return new WorldHandshakeMessage(in.readInt()); 
	}
}