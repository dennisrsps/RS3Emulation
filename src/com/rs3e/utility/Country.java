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
 * Country.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public final class Country {

	public static final int FLAG_UK          = 77;
	public static final int FLAG_USA         = 225;
	public static final int FLAG_CANADA      = 38;
	public static final int FLAG_NETHERLANDS = 161;
	public static final int FLAG_AUSTRALIA   = 16;
	public static final int FLAG_SWEDEN      = 191;
	public static final int FLAG_FINLAND     = 69;
	public static final int FLAG_IRELAND     = 101;
	public static final int FLAG_BELGIUM     = 22;
	public static final int FLAG_NORWAY      = 162;
	public static final int FLAG_DENMARK     = 58;
	public static final int FLAG_BRAZIL      = 31;
	public static final int FLAG_MEXICO      = 152;

	private final int flag;
	private final String name;

	public Country(int flag, String name) {
		this.flag = flag;
		this.name = name;
	}

	public int getFlag() {
		return flag;
	}

	public String getName() {
		return name;
	}

}
