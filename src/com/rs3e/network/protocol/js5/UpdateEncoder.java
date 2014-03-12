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

import com.rs3e.network.protocol.ondemand.FileResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToByteEncoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An {@link ByteToByteEncoder} that is used to handle the update encoding.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class UpdateEncoder extends MessageToByteEncoder<FileResponse> {

	/**
	 * Constructs a new {@code UpdateEncoder} instance.
	 */
	public UpdateEncoder() {
		super(FileResponse.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.
	 * ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, FileResponse response,
			ByteBuf buf) throws Exception {
		ByteBuf container = response.getContainer();
		int type = response.getType();
		int file = response.getFile();

		int compression = container.readUnsignedByte();
		int size = ((container.readByte() & 0xff) << 24)
				+ ((container.readByte() & 0xff) << 16)
				+ ((container.readByte() & 0xff) << 8)
				+ (container.readByte() & 0xff);
		if (!response.isPriority()) {
			file |= 0x80000000;
		}

		buf.writeByte(type);
		buf.writeInt(file);
		buf.writeByte(compression);
		buf.writeInt(size);

		int bytes = container.readableBytes();
		if (bytes > 502) {
			bytes = 502;
		}

		buf.writeBytes(container.readBytes(bytes));

		for (;;) {
			bytes = container.readableBytes();
			if (bytes == 0) {
				break;
			} else if (bytes > 507) {
				bytes = 507;
			}
			buf.writeByte(type);
			buf.writeInt(file);
			buf.writeBytes(container.readBytes(bytes));

		}
	}
}
