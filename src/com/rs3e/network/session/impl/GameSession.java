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

import io.netty.channel.ChannelHandlerContext;

import com.rs3e.network.session.Session;
import com.rsps.game.player.Player;
import com.rsps.io.Packet;
import com.rsps.io.PacketReader;
import com.rsps.net.codec.world.GamePacketHandler;

/**
 * 
 * RS3Emulation
 * GameSession.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class GameSession extends Session {
	
	private Player player;

	public GameSession(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void message(Object obj) {
		if (obj instanceof Packet) {
			PacketReader reader = new PacketReader((Packet) obj);
			GamePacketHandler.processPacket(player, reader);
		}
	}

	public void setPlayer (Player p) {
		this.player = p;
	}
}
