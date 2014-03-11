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
package com.rs3e.network.protocol.js5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToByteEncoder;

/**
 * An {@link ByteToByteEncoder} that is used to handle the XOR encoding.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class XorEncoder extends ByteToByteEncoder {

	private int key = 0;

	public void setKey(int key) {
		this.key = key;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
		while (in.readable()) {
			out.writeByte(in.readUnsignedByte() ^ key);
		}
	}
}
