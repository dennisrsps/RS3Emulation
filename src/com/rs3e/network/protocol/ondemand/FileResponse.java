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
package com.rs3e.network.protocol.ondemand;

import io.netty.buffer.ByteBuf;

/**
 * 
 * RS3Emulation
 * FileResponse.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public final class FileResponse {

	private final boolean priority;
	private final int type, file;
	private final ByteBuf container;

	public FileResponse(boolean priority, int type, int file, ByteBuf container) {
		this.priority = priority;
		this.type = type;
		this.file = file;
		this.container = container;
	}

	public boolean isPriority() {
		return priority;
	}

	public int getType() {
		return type;
	}

	public int getFile() {
		return file;
	}

	public ByteBuf getContainer() {
		return container;
	}

}
