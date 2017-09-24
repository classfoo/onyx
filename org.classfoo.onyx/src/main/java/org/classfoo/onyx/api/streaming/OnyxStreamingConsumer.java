package org.classfoo.onyx.api.streaming;

/**
 * Onyx Streaming Consumer
 * @author ClassFoo
 *
 */
public interface OnyxStreamingConsumer {

	/**
	 * start the consumer thread
	 */
	public void start();

	/**
	 * consumer a message
	 * @param message
	 */
	public void consumer(OnyxStreamingMessage message);

	/**
	 * regist a consumer message handler listener
	 * @param listener
	 */
	public void registListener(OnyxStreamingMessageListener listener);

	/**
	 * shut down the consumer
	 */
	public void shutdown();
}
