package com.rs3e.ondemand;

import java.util.ArrayDeque;
import java.util.Queue;

import com.rs3e.network.session.impl.UpdateSession;

public final class UpdateService implements Runnable {

	private final Queue<UpdateSession> pendingSessions = new ArrayDeque<>(); 

	public void addPendingSession(UpdateSession session) {
		synchronized (pendingSessions) {
			pendingSessions.add(session);
			pendingSessions.notifyAll();
		}
	}

	@Override
	public void run() {
		for (;;) {
			UpdateSession session;

			synchronized (pendingSessions) {
				while ((session = pendingSessions.poll()) == null) {
					try {
						pendingSessions.wait();
					} catch (InterruptedException e) {
						/* ignore */
					}
				}
			}

			session.processFileQueue();
		}
	}

}
