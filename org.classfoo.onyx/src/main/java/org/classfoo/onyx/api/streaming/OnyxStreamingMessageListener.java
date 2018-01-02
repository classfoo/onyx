package org.classfoo.onyx.api.streaming;

/**
 * Onyx Streaming Messsage Listener
 * @author ClassFoo
 *
 */
public interface OnyxStreamingMessageListener {

    /**
     * on consumer start
     * @param consumer
     */
    public void onStart(OnyxStreamingConsumer consumer);

    /**
     * on consumer message
     * @param consumer
     * @param message
     */
    public void onMessage(OnyxStreamingConsumer consumer, OnyxStreamingMessage message);

    /**
     * on consumer shut down
     * @param consumer
     */
    public void onShutdown(OnyxStreamingConsumer consumer);
}
