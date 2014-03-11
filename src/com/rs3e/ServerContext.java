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
package com.rs3e;

/**
 * An simple class that is used to store the context information of the Server.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class ServerContext {

	/**
	 * The host address.
	 */
	private String host;
	
	/**
	 * The port and supported client version.
	 */
	private int port, version;
	
	/**
	 * Constructs a new {@code ServerContext} instance.
	 * @param host The host of the server address.
	 * @param port The port the server is hosted on.
	 * @param version The supported client version of the server.
	 */
	public ServerContext(String host, int port, int version) {
		this.host = host;
		this.port = port;
		this.version = version;
	}

	/**
	 * Gets the host.
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port.
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the version.
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

}
