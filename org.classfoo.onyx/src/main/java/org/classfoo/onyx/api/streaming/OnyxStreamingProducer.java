package org.classfoo.onyx.api.streaming;

/**
 * Onyx Streaming Producer
 * @author ClassFoo
 *
 */
public interface OnyxStreamingProducer {

    /**
     * start a streaming producer
     */
    public void start();

    /**
     * sending message into the streaming
     * @param message
     */
    public void send(OnyxStreamingMessage message);

    /**
     * sending message into the stream with callback
     * @param message
     * @param callback
     */
    public void send(OnyxStreamingMessage message, OnyxStreamingSendCallBack callback);

    /**
     * shutdown a streaming producer
     */
    public void shutdown();
}
