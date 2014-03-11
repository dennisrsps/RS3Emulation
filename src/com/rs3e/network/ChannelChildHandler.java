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
package com.rs3e.network;

import com.rs3e.Main;
import com.rs3e.network.protocol.handshake.HandshakeDecoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * An {@link ChannelInitializer} that is used to initialize an incoming channel
 * request that is sent to the sever.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
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
