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
package com.rs3e.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * RS3Emulation
 * Session.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public abstract class Session {

	/**
	 * The channel.
	 */
	protected final Channel channel;
	
	/**
	 * The channel handler context.
	 */
	protected final ChannelHandlerContext context;
	
	/**
	 * Creates a new Session.
	 * @param context The channel handler context.
	 */
	public Session(ChannelHandlerContext context) {
		this.channel = context.channel();
		this.context = context;
	}
	
	/**
	 * Handles the disconnection of a certain channel.
	 */
	public abstract void disconnected();
	
	/**
	 * Gets the channel.
	 * @return The channel.
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the channel handler context.
	 * @return The channel handler context.
	 */
	public ChannelHandlerContext getContext() {
		return context;
	}

	/**
	 * Receives the message fired from the frame decoder.
	 * @param obj The object fired.
	 */
	public abstract void message(Object obj);
}
