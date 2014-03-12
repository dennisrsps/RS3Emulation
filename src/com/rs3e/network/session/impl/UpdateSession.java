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
package com.rs3e.network.session.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.rs3e.Constants;
import com.rs3e.Main;
import com.rs3e.cache.RS3Cache;
import com.rs3e.network.protocol.js5.XorEncoder;
import com.rs3e.network.protocol.messages.UpdateEncryptionMessage;
import com.rs3e.network.protocol.messages.UpdateStatusMessage;
import com.rs3e.network.protocol.messages.UpdateVersionMessage;
import com.rs3e.network.protocol.ondemand.FileRequest;
import com.rs3e.network.protocol.ondemand.FileResponse;
import com.rs3e.network.protocol.ondemand.UpdateService;
import com.rs3e.network.session.Session;

/**
 * 
 * RS3Emulation
 * UpdateSession.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class UpdateSession extends Session {

	/**
	 * The main context.
	 */
	private final Main mainContext;

	/**
	 * The update service.
	 */
	private final UpdateService service;

	/**
	 * An {@link Deque} used to handle file queued requests.
	 */
	private final Deque<FileRequest> fileQueue = new ArrayDeque<>();

	/**
	 * The flag for the session is idle.
	 */
	private boolean idle = true;

	/**
	 * The handshake complete flag.
	 */
	private boolean handshakeComplete = false;

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(UpdateSession.class
			.getName());

	/**
	 * Constructs a new {@code UpdateSession} instance.
	 * 
	 * @param ctx
	 *            The context of the channel.
	 * @param mainContext
	 *            The main context of the server.
	 */
	public UpdateSession(ChannelHandlerContext ctx, Main mainContext) {
		super(ctx);
		this.mainContext = mainContext;
		this.service = mainContext.getUpdateService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rs3e.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
		fileQueue.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rs3e.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		if (handshakeComplete) {
			if (obj instanceof FileRequest) {
				FileRequest request = (FileRequest) obj;

				synchronized (fileQueue) {
					if (request.isPriority()) {
						fileQueue.addFirst(request);
					} else {
						fileQueue.addLast(request);
					}

					if (idle) {
						service.addPendingSession(this);
						idle = false;
					}
				}
			} else if (obj instanceof UpdateEncryptionMessage) {
				UpdateEncryptionMessage encryption = (UpdateEncryptionMessage) obj;
				XorEncoder encoder = channel.pipeline().get(XorEncoder.class);
				encoder.setKey(encryption.getKey());
			}
		} else {
			UpdateVersionMessage version = (UpdateVersionMessage) obj;

			int status;
			if (version.getVersion() == Constants.ServerRevision) {
				status = UpdateStatusMessage.STATUS_OK;
			} else {
				status = UpdateStatusMessage.STATUS_OUT_OF_DATE;
			}

			ChannelFuture future = channel
					.write(new UpdateStatusMessage(status));
			if (status == UpdateStatusMessage.STATUS_OK) {
				/*
				 * the client won't re-connect so an ondemand session cannot
				 * time out
				 */
				channel.pipeline().remove(ReadTimeoutHandler.class);
				handshakeComplete = true;
			} else {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	/**
	 * Process' the file queue.
	 */
	public void processFileQueue() {
		FileRequest request;

		synchronized (fileQueue) {
			request = fileQueue.pop();
			if (fileQueue.isEmpty()) {
				idle = true;
			} else {
				service.addPendingSession(this);
				idle = false;
			}
		}

		if (request != null) {
			int type = request.getType();
			int file = request.getFile();

			RS3Cache cache = mainContext.getCache();
			ByteBuf buf;

			try {
				if (type == 255 && file == 255)
					buf = Unpooled
							.wrappedBuffer(mainContext.getChecksumTable());
				else {
					buf = Unpooled.wrappedBuffer(cache.getStore().read(type,
							file));
					if (type != 255)
						buf = buf.slice(0, buf.readableBytes() - 2);
				}
				channel.write(new FileResponse(request.isPriority(), type,
						file, buf));
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Failed to service file request "
						+ type + ", " + file + ".", ex);
			}
		}
	}

	/**
	 * Gets the main context.
	 * 
	 * @return the context
	 */
	public Main getMainContext() {
		return mainContext;
	}
}
