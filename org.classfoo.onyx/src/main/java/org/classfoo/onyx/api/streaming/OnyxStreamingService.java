package org.classfoo.onyx.api.streaming;

import java.util.List;

/**
 * Onyx Streaming Service
 * @author ClassFoo
 *
 */
public interface OnyxStreamingService {

    /**
     * create streaming producer
     * @param streaming
     * @return
     */
    public OnyxStreamingProducer createProducer(String streaming);

    /**
     * create streaming consumer
     * @param streaming
     * @return
     */
    public OnyxStreamingConsumer createConsumer(String streaming);

    /**
     * create message
     * @param streaming
     * @param message
     * @return
     */
    public OnyxStreamingMessage createMessage(String streaming, Object message);

    /**
     * get consumers by streaming
     * @param streaming
     * @return
     */
    public List<OnyxStreamingConsumer> getConsumers(String streaming);

    /**
     * get producers by name
     * @param streaming
     * @return
     */
    public List<OnyxStreamingProducer> getProducers(String streaming);

}
