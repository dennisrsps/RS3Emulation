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
import com.rs3e.network.protocol.messages.HandshakeMessage;
import com.rs3e.network.session.Session;
import com.rs3e.network.session.impl.LoginSession;
import com.rs3e.network.session.impl.UpdateSession;
import com.rs3e.network.session.impl.WorldListSession;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * 
 * RS3Emulation
 * ServerChannelAdapterHandler.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class ServerChannelAdapterHandler extends ChannelInboundMessageHandlerAdapter<Object> {
	
	/**
	 * An {@link AttributeKey} that is used for storing attributes of a specific channel.
	 */
	private static final AttributeKey<Session> attributeMap = new AttributeKey<Session>("ServerChannelAdapterHandler.attr");
	
	/**
	 * The main context of the server.
	 */
	private Main mainContext;
	
	/**
	 * Constructs a new {@code ServerChannelAdapterHandler} instance.
	 * @param mainContext The main context of the server.
	 */
	public ServerChannelAdapterHandler(Main mainContext) {
		this.mainContext = mainContext;
	}
	
	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundMessageHandlerAdapter#messageReceived(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		Object attachment = ctx.channel().attr(attributeMap).get();
		if(attachment == null) {
			HandshakeMessage handshakeMessage = (HandshakeMessage) msg;
			switch(handshakeMessage.getState()) {
			case HANDSHAKE_UPDATE:
				ctx.channel().attr(attributeMap).set(new UpdateSession(ctx, mainContext));
				break;
			case HANDSHAKE_WORLD_LIST:
				ctx.channel().attr(attributeMap).set(new WorldListSession(ctx));
				break;
			case HANDSHAKE_LOGIN:
				ctx.channel().attr(attributeMap).set(new LoginSession(ctx));
				break;
				default: throw new IllegalStateException("Invalid handshake state requested.");
			}
		} else
			((Session) ctx.channel().attr(attributeMap).get()).message(msg);
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelStateHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
		t.printStackTrace();
		ctx.channel().close();
	}
}
