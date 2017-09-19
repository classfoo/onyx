package org.classfoo.onyx.api.streaming;

import org.classfoo.onyx.impl.storage.datas.neeq.NEEQDataConsumer_CompanyInfo;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Onyx Streaming Service
 * @author ClassFoo
 *
 */
public interface OnyxStreamingService {

	public OnyxStreamingProducer createProducer();

	public <T extends OnyxStreamingConsumer> T createConsumer(String name, Class<T> clazz);

	public OnyxStreamingProducer startCsvStreaming(String name, CSVReader reader);

	public void addConsumer(String name, OnyxStreamingConsumer consumer);
}
