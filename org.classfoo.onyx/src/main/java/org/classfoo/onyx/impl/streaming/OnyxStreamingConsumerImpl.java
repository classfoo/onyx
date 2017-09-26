package org.classfoo.onyx.impl.streaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingContext;
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

	private Map<String, OnyxStreamingMessageListener> listeners;

	private Queue<OnyxStreamingMessage> queue = new LinkedBlockingQueue<OnyxStreamingMessage>();

	private AtomicBoolean closed = new AtomicBoolean(true);

	private long startTime;

	private long messageCount;

	private OnyxStreamingContextImpl context;

	public OnyxStreamingConsumerImpl(OnyxService onyxService, String name) {
		this.onyxService = onyxService;
		this.name = name;
		this.context = new OnyxStreamingContextImpl();
	}

	@Override
	public void start() {
		this.closed.set(false);
		Thread thread = new Thread(this, "OnyxStreamingThread");
		thread.start();
		this.startTime = System.currentTimeMillis();
		this.messageCount = 0;
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
	public OnyxStreamingContext getContext() {
		return this.context;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		while (!closed.get()) {
			try {
				OnyxStreamingMessage message = removeFromQueue();
				if (message == null) {
					continue;
				}
				this.onMessage(message);
				messageCount++;
				if (messageCount % 1000 == 0) {
					long current = System.currentTimeMillis();
					long period = current - startTime;
					logger.info("流‘{}’已消费{}行数据，耗时：{}秒，速度：{}行/秒...", message.getStreaming(), messageCount,
							((double) period) / 1000, 1000 * 1000 / (period == 0 ? 1 : period));
					startTime = current;
				}
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
			this.wait(1000);
		}
		return queue.poll();
	}

	@Override
	public void registListener(String streaming, OnyxStreamingMessageListener listener) {
		if (this.listeners == null) {
			this.listeners = new HashMap<String, OnyxStreamingMessageListener>(10);
		}
		this.listeners.put(streaming, listener);
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
		for (OnyxStreamingMessageListener listener : this.listeners.values()) {
			listener.onStart(this);
		}
	}

	private void onMessage(OnyxStreamingMessage message) {
		if (this.listeners == null || this.listeners.isEmpty()) {
			return;
		}
		String streaming = message.getStreaming();
		OnyxStreamingMessageListener listener = this.listeners.get(streaming);
		if (listener != null) {
			listener.onMessage(this, message);
		}
	}

	private void onShutdown() {
		if (this.listeners == null || this.listeners.isEmpty()) {
			return;
		}
		for (OnyxStreamingMessageListener listener : this.listeners.values()) {
			listener.onShutdown(this);
		}
	}
}
