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

import com.rs3e.network.protocol.messages.WorldListMessage;
import com.rs3e.utility.ByteBufUtils;
import com.rs3e.utility.Country;
import com.rs3e.utility.World;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An {@link MessageToByteEncoder} to handle the data of the world list.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class WorldListEncoder extends MessageToByteEncoder<WorldListMessage> {

	/**
	 * Constructs a new {@link WorldListEncoder} instance.
	 */
	public WorldListEncoder() {
		super(WorldListMessage.class);
	}
	
	/* (non-Javadoc)
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, WorldListMessage list, ByteBuf out) throws Exception {
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(1);
		buf.writeByte(1);

		Country[] countries = list.getCountries();
		ByteBufUtils.writeSmart(buf, countries.length);
		for (Country country : countries) {
			ByteBufUtils.writeSmart(buf, country.getFlag());
			ByteBufUtils.writeWorldListString(buf, country.getName());
		}

		World[] worlds = list.getWorlds();
		int minId = worlds[0].getId();
		int maxId = worlds[0].getId();
		for (int i = 1; i < worlds.length; i++) {
			World world = worlds[i];
			int id = world.getId();

			if (id > maxId)
				maxId = id;
			if (id < minId)
				minId = id;
		}

		ByteBufUtils.writeSmart(buf, minId);
		ByteBufUtils.writeSmart(buf, maxId);
		ByteBufUtils.writeSmart(buf, worlds.length);

		for (World world : worlds) {
			ByteBufUtils.writeSmart(buf, world.getId() - minId);
			buf.writeByte(world.getCountry());
			buf.writeInt(world.getFlags());
			ByteBufUtils.writeWorldListString(buf, world.getActivity());
			ByteBufUtils.writeWorldListString(buf, world.getIp());
		}

		buf.writeInt(list.getSessionId());
		for (int i = 0; i < worlds.length; i++) {
			World world = worlds[i];
			ByteBufUtils.writeSmart(buf, world.getId() - minId);
			buf.writeShort(0);
		}

		out.writeByte(0); // 0 = ok, 7/9 = world full
		out.writeShort(buf.readableBytes());
		out.writeBytes(buf);		
	}
}
