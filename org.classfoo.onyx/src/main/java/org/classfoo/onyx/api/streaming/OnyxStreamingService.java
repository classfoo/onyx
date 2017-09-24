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
	 * @param name
	 * @return
	 */
	public OnyxStreamingProducer createProducer(String name);

	/**
	 * create streaming consumer
	 * @param name
	 * @return
	 */
	public OnyxStreamingConsumer createConsumer(String name);

	/**
	 * create message
	 * @param message
	 * @return
	 */
	public OnyxStreamingMessage createMessage(Object message);

	/**
	 * get consumers by name
	 * @param name
	 * @return
	 */
	public List<OnyxStreamingConsumer> getConsumers(String name);

	/**
	 * get producers by name
	 * @param name
	 * @return
	 */
	public List<OnyxStreamingProducer> getProducers(String name);

}
