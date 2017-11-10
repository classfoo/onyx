package org.classfoo.onyx.impl.streaming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateService;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingProducer;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @see OnyxStreamingService
 * @author ClassFoo
 *
 */
@Component
public class OnyxStreamingServiceImpl implements OnyxStreamingService {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStreamingServiceImpl.class);

	@Autowired
	private OnyxService onyxService;

	@Autowired
	private OnyxStorageService storageService;

	@Autowired
	private OnyxOperateService operateService;

	private Map<String, List<OnyxStreamingProducer>> producers = new HashMap<String, List<OnyxStreamingProducer>>(10);

	private Map<String, List<OnyxStreamingConsumer>> consumers = new HashMap<String, List<OnyxStreamingConsumer>>(10);

	@Override
	public OnyxStreamingProducer createProducer(String name) {
		OnyxStreamingProducerImpl producer = new OnyxStreamingProducerImpl(this.onyxService, name);
		List<OnyxStreamingProducer> producers = this.producers.get(name);
		if (producers == null) {
			producers = new ArrayList<OnyxStreamingProducer>(20);
			this.producers.put(name, producers);
		}
		producers.add(producer);
		return producer;
	}

	@Override
	public List<OnyxStreamingProducer> getProducers(String name) {
		return this.producers.get(name);
	}

	@Override
	public OnyxStreamingConsumer createConsumer(String name) {
		OnyxStreamingConsumer consumer = new OnyxStreamingConsumerImpl(this.onyxService, name);
		List<OnyxStreamingConsumer> consumers = this.consumers.get(name);
		if (consumers == null) {
			consumers = new ArrayList<OnyxStreamingConsumer>(20);
			this.consumers.put(name, consumers);
		}
		consumers.add(consumer);
		return consumer;
	}

	@Override
	public List<OnyxStreamingConsumer> getConsumers(String name) {
		return this.consumers.get(name);
	}

	@Override
	public OnyxStreamingMessage createMessage(String streaming, Object message) {
		return new OnyxStreamingMessageImpl(streaming, message);
	}

}
