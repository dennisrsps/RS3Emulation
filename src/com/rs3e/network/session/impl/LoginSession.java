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
package com.rs3e.network.session.impl;

import com.rs3e.network.session.Session;

import io.netty.channel.ChannelHandlerContext;

/**
 * An {@link Session} that handles the login request.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginSession extends Session {

	/**
	 * Constructs a new {@link LoginSession} instance.
	 * @param context The context of the channel.
	 */
	public LoginSession(ChannelHandlerContext context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see com.rs3e.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {

	}

	/* (non-Javadoc)
	 * @see com.rs3e.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object message) {

	}
}
