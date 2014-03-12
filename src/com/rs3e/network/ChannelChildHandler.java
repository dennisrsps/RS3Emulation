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
package com.rs3e.network;

import com.rs3e.Main;
import com.rs3e.network.protocol.handshake.HandshakeDecoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 
 * RS3Emulation
 * ChannelChildHandler.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class ChannelChildHandler extends ChannelInitializer<SocketChannel> {

	/**
	 * The main context of the server.
	 */
	private Main mainContext;
	
	/**
	 * Constructs a new {@code ChannelChildHandler} instance.
	 * @param mainContext The main context of the server.
	 */
	public ChannelChildHandler(Main mainContext) {
		this.mainContext = mainContext;
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new ReadTimeoutHandler(5), new HandshakeDecoder(), new ServerChannelAdapterHandler(mainContext));
	}
}
