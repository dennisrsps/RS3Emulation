package com.rs3e.network.protocol.login;

import com.rs3e.network.protocol.messages.LoginResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Belthazar - 731 Updates
 * @author Sir Tom - 530 Base
 */
public class LoginEncoder extends MessageToByteEncoder<LoginResponse> {
	
	/**
	 * Constructs a new {@link LoginEncoder} instance.
	 */
	public LoginEncoder() {
		super(LoginResponse.class);
	}

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, LoginResponse msg, ByteBuf out) throws Exception {
		out.writeByte(msg.getReturnType().returnCode);
		if (msg.hasPayload()) {
			out.writeByte(msg.getPayloadSize());
			out.writeBytes(msg.getPayload());
		}
		/*out.writeByte(player.getReturnType());
		if(player.getReturnCode() == 2) {
			out.writeByte(player.getRights());
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeShort(player.getIndex());
			out.writeByte(1);
			out.writeByte(1);
			out.writeByte(2);
			out.writeByte(player.getRights());
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(1);
			out.writeByte(0);
			out.writeShort(player.getIndex());
			out.writeByte(1);
			//out.write24BitInteger(0);
			out.writeByte(1); //is member world
			OutputStream.writeString(out, player.getDisplayName());
			//out.endPacketVarByte();
		}*/
	}
}
