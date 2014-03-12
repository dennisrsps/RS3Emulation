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
package com.rs3e;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import com.rs3e.cache.Container;
import com.rs3e.cache.FileStore;
import com.rs3e.cache.RS3Cache;
import com.rs3e.network.ChannelChildHandler;
import com.rs3e.network.protocol.ondemand.UpdateService;

/**
 * RS3Emulation
 * Main.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class Main {

	/**
	 * The {@link ServerBootstrap} that is used to handle game networking.
	 */
	private ServerBootstrap bootstrap;

	/**
	 * The cache instance for the server.
	 */
	private RS3Cache cache;

	/**
	 * The default logging instance.
	 */
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	/**
	 * The update service for incoming requests.
	 */
	private final UpdateService updateService = new UpdateService();

	/**
	 * The executor for the update service
	 */
	private final Executor serviceExecutor = Executors.newCachedThreadPool();

	/**
	 * The checksum table instance.
	 */
	private ByteBuffer checksumTable;

	/**
	 * Constructs a new {@code Main} instance.
	 */
	public Main() {
		logger.info("Launching " + Constants.ServerName + "...");
	}

	/**
	 * The main method of the Ieldor server, this is where everything get
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String... args) {
		Main mainContext = null;
		try {
			mainContext = new Main();
			mainContext.initate();
			mainContext.loadGameContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Complete!");
	}

	/**
	 * Initates a new gaming enviroment, here we are going to setup everything
	 * needed for the server to be able to bind.
	 */
	private void initate() {
		bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 100);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new ChannelChildHandler(this));
		try {
			bootstrap.localAddress(Constants.ServerPort).bind().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Here we are going to load any context that is needed for the game to be
	 * successfully prepared for clients to connect. For example, here we are
	 * going to load item definitions, and the client cache for the client to
	 * store in the local cache storage directory.
	 * 
	 * @throws IOException
	 */
	private void loadGameContext() throws IOException {
		this.cache = new RS3Cache(FileStore.open(new File("./data/cache/")));
		serviceExecutor.execute(updateService);

		Container container = new Container(Container.COMPRESSION_NONE, cache.createChecksumTable().encode(true, Constants.JS5ModulusKey, Constants.JS5PrivateKey));
		checksumTable = container.encode();
	}

	/**
	 * Gets the cache instance.
	 * 
	 * @return the cache
	 */
	public RS3Cache getCache() {
		return cache;
	}

	/**
	 * Gets the update service.
	 * 
	 * @return the update service
	 */
	public UpdateService getUpdateService() {
		return updateService;
	}

	/**
	 * Gets the {@link ByteBuffer} (checksum table) for the cache.
	 * 
	 * @return the checksum table
	 */
	public ByteBuffer getChecksumTable() {
		return checksumTable;
	}
}
