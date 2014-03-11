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

import com.rs3e.network.protocol.messages.UpdateStatusMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An {@link MessageToByteEncoder} that writes the response of the message
 * status.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class UpdateStatusEncoder extends
		MessageToByteEncoder<UpdateStatusMessage> {

	/**
	 * The delta for the update verification.
	 */
	public static final int[] UPDATE_DATA = { 2294, 69795, 41433, 35748,
			358716, 44375, 0, 18361, 22721, 119016, 966168, 330618, 472621,
			621699, 908236, 27551, 588732, 18398, 1244, 35523, 1973, 119,
			1098747, 2731236, 7844, 21415 };

	/**
	 * Constructs a new {@code UpdateStatusEncoder} instance.
	 */
	public UpdateStatusEncoder() {
		super(UpdateStatusMessage.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.
	 * ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, UpdateStatusMessage msg,
			ByteBuf out) throws Exception {
		for (int i = 0; i < UPDATE_DATA.length; i++)
			out.writeInt(UPDATE_DATA[i]);
		out.writeByte(msg.getStatus());
	}
}
