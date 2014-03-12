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

/**
 * 
 * RS3Emulation
 * World.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public final class World {

	public static final int FLAG_MEMBERS    = 0x1;
	public static final int FLAG_QUICK_CHAT = 0x2;
	public static final int FLAG_PVP        = 0x4;
	public static final int FLAG_LOOT_SHARE = 0x8;
	public static final int FLAG_HIGHLIGHT  = 0x10;

	private final int id, flags, country;
	private final String activity, ip;

	public World(int id, int flags, int country, String activity, String ip) {
		this.id = id;
		this.flags = flags;
		this.country = country;
		this.activity = activity;
		this.ip = ip;
	}

	public int getId() {
		return id;
	}

	public int getFlags() {
		return flags;
	}

	public int getCountry() {
		return country;
	}

	public String getActivity() {
		return activity;
	}

	public String getIp() {
		return ip;
	}

}
