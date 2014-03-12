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
package com.rs3e.utility;

import java.math.BigInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 
 * RS3Emulation
 * ByteBufUtils.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public final class ByteBufUtils {

	public static ByteBuf rsa(ByteBuf buf, String modulus, String exponent) {
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);

		BigInteger cipherText = new BigInteger(bytes);
		BigInteger mod = new BigInteger(modulus);
		BigInteger exp = new BigInteger(exponent);
		BigInteger plainText = cipherText.modPow(exp, mod);

		return Unpooled.wrappedBuffer(plainText.toByteArray());
	}

	public static String readString(ByteBuf buffer) {
		buffer.markReaderIndex();

		int len = 0;
		while (buffer.readUnsignedByte() != 0)
			len++;

		buffer.resetReaderIndex();

		byte[] bytes = new byte[len];
		buffer.readBytes(bytes);
		buffer.readerIndex(buffer.readerIndex() + 1);
		return new String(bytes, Charsets.ASCII_LATIN1_CHARSET);
	}

	public static void writeString(ByteBuf buffer, String str) {
		byte[] bytes = str.getBytes(Charsets.ASCII_LATIN1_CHARSET);
		buffer.writeBytes(bytes);
		buffer.writeByte(0);
	}

	public static void writeSmart(ByteBuf buffer, int value) {
		if (value < 128)
			buffer.writeByte(value);
		else
			buffer.writeShort(32768 + value);
	}

	public static void writeWorldListString(ByteBuf buffer, String str) {
		buffer.writeByte(0);
		buffer.writeBytes(str.getBytes(Charsets.ASCII_LATIN1_CHARSET));
		buffer.writeByte(0);
	}

	private ByteBufUtils() {

	}

}