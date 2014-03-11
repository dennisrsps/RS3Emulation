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
package com.rs3e.network.protocol.handshake;

/**
 * An simple enumeration that is used to store the possible stages of handshake.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public enum HandshakeState {

	HANDSHAKE_UPDATE, HANDSHAKE_LOGIN, HANDSHAKE_WORLD_LIST;
	
	/**
	 * Gets the state of handshake based on the ID.
	 * @param opcode The opcode.
	 */
	public static HandshakeState forId(int opcode) {
		if (opcode == 15)
			return HANDSHAKE_UPDATE;
		else if (opcode == 14)
			return HANDSHAKE_LOGIN;
		else if (opcode == 255)
			return HANDSHAKE_WORLD_LIST;
		return null;
	}
}
