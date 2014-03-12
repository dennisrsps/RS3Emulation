/*
 * This file is part of RS3Emulation.
 *
 * RS3Emulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RS3Emulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RS3Emulation.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rs3e.network.session.impl;

import com.rs3e.network.protocol.messages.WorldListMessage;
import com.rs3e.network.session.Session;
import com.rs3e.utility.Country;
import com.rs3e.utility.World;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * RS3Emulation
 * WorldListSession.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class WorldListSession extends Session {

	/**
	 * The current countries of the server.
	 */
	private static final Country[] COUNTRIES = {
			new Country(Country.FLAG_USA, "USA"),
			new Country(Country.FLAG_AUSTRALIA, "Australia") };

	/**
	 * Constructs a new {@link WorldListSession} instance.
	 * 
	 * @param context
	 *            The context of the channel.
	 */
	public WorldListSession(ChannelHandlerContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rs3e.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
		/*
		 * Nothing to do here.
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rs3e.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		World[] worlds = {
				new World(1, World.FLAG_MEMBERS | World.FLAG_QUICK_CHAT, 0,
						"RS3Emu", "127.0.0.1"),
				new World(2, World.FLAG_MEMBERS | World.FLAG_QUICK_CHAT, 0,
						"Ieldor BETA", "127.0.0.1") };
		int[] players = { 0 };
		channel.write(
				new WorldListMessage(0xDEADBEEF, COUNTRIES, worlds, players))
				.addListener(ChannelFutureListener.CLOSE);
	}
}
