package com.rs3e;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.openrs.cache.Container;
import org.openrs.cache.FileStore;
import org.openrs.cache.RS3Cache;

import com.rs3e.network.ChannelChildHandler;
import com.rs3e.ondemand.UpdateService;

public class Main {

	/**
	 * The {@link ServerBootstrap} that is used to handle game networking.
	 */
	private ServerBootstrap bootstrap;

	/**
	 * The {@link ServerContext} class for storing server information.
	 */
	private ServerContext serverContext;

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
	 * The rsa modulus key.
	 */
	public static final BigInteger MODULUS_KEY = new BigInteger(
			"92952295964155672087801015402111750591919500795257231765611346743023141259087751200849540075517787992536588533840502270599726488785462782810450546358220539026800123618215080154270389875550255531705255494326594317637443265761822792347000385003452391742987636722632882107837694393096626546031683632300323372031");

	/**
	 * The rsa private key.
	 */
	public static final BigInteger PRIVATE_KEY = new BigInteger(
			"48844029607909929282080219851655163753063517835529068275083137145982131295000747300835504541261906753238247645275176117230165873030406752131258615980047884895054026459192588976183254088467205779659961501829292823408290106695592149680053016585516891071746766448461496845037459094045320573736639805712121612241");

	/**
	 * The release number (supported client build).
	 */
	private static final int RELEASE_NUMBER = 795;

	/**
	 * The address the server will be hosted on.
	 */
	private static final String HOST = "127.0.0.1";

	/**
	 * arg[1]: (int : port) - The specific port that the server is hosted on.
	 */
	private static final int PORT = 43594;

	/**
	 * Constructs a new {@code Main} instance.
	 */
	public Main() {
		logger.info("Launching the RS3Emulator...");
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
			mainContext.initate(HOST, PORT);
			mainContext.bind();

			mainContext.setServerContext(new ServerContext(HOST, PORT,
					RELEASE_NUMBER));
			mainContext.loadGameContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Complete!");
	}

	/**
	 * Initates a new gaming enviroment, here we are going to setup everything
	 * needed for the server to be able to bind.
	 * 
	 * @param host
	 *            The host address of the server.
	 * @param port
	 *            The port of the server.
	 */
	private void initate(String host, int port) {
		bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 100);
		bootstrap.localAddress(new InetSocketAddress(host, port));
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new ChannelChildHandler(this));
	}

	/**
	 * Binds the server to the specified {@link InetSocketAddress} that was
	 * using "localAddress" via the {@link ServerBootstrap}.
	 */
	private void bind() {
		try {
			bootstrap.bind().sync();
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

		Container container = new Container(Container.COMPRESSION_NONE, cache
				.createChecksumTable().encode(true, MODULUS_KEY, PRIVATE_KEY));
		checksumTable = container.encode();
	}

	/**
	 * Sets the {@link ServerContext} instance.
	 * 
	 * @param serverContext
	 *            The instance to set to.
	 */
	private void setServerContext(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	/**
	 * Gets the server context instance.
	 * 
	 * @return The server context instance.
	 */
	public ServerContext getServerContext() {
		return serverContext;
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
