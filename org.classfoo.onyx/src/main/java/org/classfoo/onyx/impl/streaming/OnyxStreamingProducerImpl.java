package org.classfoo.onyx.impl.streaming;

import java.util.List;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingProducer;
import org.classfoo.onyx.api.streaming.OnyxStreamingSendCallBack;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;

/**
 * @see OnyxStreamingProducer
 * @author ClassFoo
 *
 */
public class OnyxStreamingProducerImpl implements OnyxStreamingProducer {

    private OnyxService onyxService;

    private OnyxStreamingService streamingService;

    private String name;

    public OnyxStreamingProducerImpl(OnyxService onyxService, String name) {
        this.onyxService = onyxService;
        this.streamingService = this.onyxService.getStreamingService();
        this.name = name;
    }

    @Override
    public void start() {
        List<OnyxStreamingConsumer> consumers = streamingService.getConsumers(this.name);
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (OnyxStreamingConsumer consumer : consumers) {
            consumer.start();
        }
    }

    @Override
    public void send(OnyxStreamingMessage message) {
        List<OnyxStreamingConsumer> consumers = streamingService.getConsumers(this.name);
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (OnyxStreamingConsumer consumer : consumers) {
            consumer.consumer(message);
        }
    }

    @Override
    public void send(OnyxStreamingMessage message, OnyxStreamingSendCallBack callback) {

    }

    @Override
    public void shutdown() {
        List<OnyxStreamingConsumer> consumers = streamingService.getConsumers(this.name);
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (OnyxStreamingConsumer consumer : consumers) {
            consumer.shutdown();
        }
    }

}
