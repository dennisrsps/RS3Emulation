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

import com.rs3e.network.protocol.messages.UpdateEncryptionMessage;
import com.rs3e.network.protocol.messages.UpdateVersionMessage;
import com.rs3e.ondemand.FileRequest;
import com.rs3e.utility.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * An {@link ByteToMessageDecoder} that handles the update decoding.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class UpdateDecoder extends ByteToMessageDecoder<Object> {
	
	/**
	 * An simple enumeration used for handling update stages.
	 *
	 * @author Thomas Le Godais <thomaslegodais@live.com>
	 *
	 */
	public enum UpdateStage { READ_VERSION, READ_REQUEST };
	
	/**
	 * The default update stage.
	 */
	private UpdateStage updateStage = UpdateStage.READ_VERSION;

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		if(buf.readableBytes() < 6)
			return null;
		
		if (updateStage == UpdateStage.READ_VERSION) {
			updateStage = UpdateStage.READ_REQUEST;
			int length = buf.readUnsignedByte();
			if(buf.readableBytes() >= length) {
				int version = buf.readInt();
				int subVersion = buf.readInt();
				String key = ByteBufUtils.readString(buf);				
				return new UpdateVersionMessage(version, subVersion, key);
			}
		} else {
			int opcode = buf.readUnsignedByte();
			if (opcode == 0 || opcode == 1) {
				int type = buf.readUnsignedByte();
				int file = buf.readInt();
				return new FileRequest(opcode == 1, type, file);
			} else if (opcode == 4) {
				int key = buf.readUnsignedByte();
				buf.readerIndex(buf.readerIndex() + 2);
				return new UpdateEncryptionMessage(key);
			} else {
				buf.readerIndex(buf.readerIndex() + 5);
				return null;
			}
		}
		return null;
	}
}
