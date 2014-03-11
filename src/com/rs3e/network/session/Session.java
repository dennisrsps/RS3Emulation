package com.rs3e.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Sean
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
