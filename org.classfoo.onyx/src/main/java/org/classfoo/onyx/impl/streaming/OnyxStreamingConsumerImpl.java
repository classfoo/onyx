package org.classfoo.onyx.impl.streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see OnyxStreamingConsumer
 * @author ClassFoo
 *
 */
public class OnyxStreamingConsumerImpl implements OnyxStreamingConsumer, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStreamingConsumerImpl.class);

	private String name;

	private OnyxService onyxService;

	private List<OnyxStreamingMessageListener> listeners;

	private Queue<OnyxStreamingMessage> queue = new ConcurrentLinkedQueue<OnyxStreamingMessage>();

	private AtomicBoolean closed = new AtomicBoolean(true);

	public OnyxStreamingConsumerImpl(OnyxService onyxService, String name) {
		this.onyxService = onyxService;
		this.name = name;
	}

	@Override
	public void start() {
		this.closed.set(false);
		Thread thread = new Thread(this, "OnyxStreamingThread");
		thread.start();
		this.onStart();
	}

	@Override
	public synchronized void consumer(OnyxStreamingMessage message) {
		if (this.closed.get()) {
			this.start();
		}
		this.queue.add(message);
		notifyAll();
	}

	@Override
	public void run() {
		while (!closed.get()) {
			try {
				OnyxStreamingMessage message = removeFromQueue();
				if (message == null) {
					continue;
				}
				this.onMessage(message);
				if (queue.isEmpty()) {
					this.stop();
				}
			}
			catch (Exception e) {
				logger.error("记录DIFlowSession会话信息失败!", e);
			}
		}
	}

	private synchronized OnyxStreamingMessage removeFromQueue() throws InterruptedException {
		if (this.closed.get()) {
			return null;
		}
		while (queue.size() == 0) {
			if (this.closed.get()) {
				return null;
			}
			this.wait(3 * 1000);
		}
		return queue.poll();
	}

	@Override
	public void registListener(OnyxStreamingMessageListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<OnyxStreamingMessageListener>(10);
		}
		this.listeners.add(listener);
	}

	/**
	 * 关闭线程
	 */
	private synchronized void stop() {
		this.closed.set(true);
		this.notify();
	}

	@Override
	public void shutdown() {
		this.stop();
		this.onShutdown();
	}

	private void onStart() {
		if (this.listeners == null || this.listeners.isEmpty()) {
			return;
		}
		for (OnyxStreamingMessageListener listener : this.listeners) {
			listener.onStart(this);
		}
	}

	private void onMessage(OnyxStreamingMessage message) {
		if (this.listeners == null || this.listeners.isEmpty()) {
			return;
		}
		for (OnyxStreamingMessageListener listener : this.listeners) {
			listener.onMessage(this, message);
		}
	}

	private void onShutdown() {
		if (this.listeners == null || this.listeners.isEmpty()) {
			return;
		}
		for (OnyxStreamingMessageListener listener : this.listeners) {
			listener.onShutdown(this);
		}
	}
}
