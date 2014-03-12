package com.rs3e.network.protocol.login;

import com.rs3e.network.protocol.messages.LoginMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LoginMessageEncoder extends MessageToByteEncoder<LoginMessage> {

	@Override
	public void encode(ChannelHandlerContext ctx, LoginMessage msg, ByteBuf out)
			throws Exception {
		out.writeByte(msg.getReturnCode());
	}

}
