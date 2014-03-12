package com.rs3e.network.session.impl;

import io.netty.channel.ChannelHandlerContext;

import com.rs3e.network.session.Session;
import com.rsps.game.player.Player;
import com.rsps.io.Packet;
import com.rsps.io.PacketReader;
import com.rsps.net.codec.world.GamePacketHandler;

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
