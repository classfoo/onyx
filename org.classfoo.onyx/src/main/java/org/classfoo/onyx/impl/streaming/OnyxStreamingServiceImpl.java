package org.classfoo.onyx.impl.streaming;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateService;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingProducer;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @see OnyxStreamingService
 * @author ClassFoo
 *
 */
@Component
public class OnyxStreamingServiceImpl implements OnyxStreamingService {

	@Autowired
	private OnyxService onyxService;

	@Autowired
	private OnyxStorageService storageService;

	@Autowired
	private OnyxOperateService operateService;

	private Map<String, List<OnyxStreamingConsumer>> consumers = new HashMap<String, List<OnyxStreamingConsumer>>(100);

	@Override
	public OnyxStreamingProducer createProducer() {
		return null;
	}

	@Override
	public <T extends OnyxStreamingConsumer> T createConsumer(String name, Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getConstructor(OnyxService.class);
			T instance = constructor.newInstance(this.onyxService);
			this.addConsumer(name, instance);
			return instance;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addConsumer(String name, OnyxStreamingConsumer consumer) {
		List<OnyxStreamingConsumer> list = this.consumers.get(name);
		if (list == null) {
			list = new ArrayList<OnyxStreamingConsumer>(10);
			this.consumers.put(name, list);
		}
		list.add(consumer);
	}

	@Override
	public OnyxStreamingProducer startCsvStreaming(String name, CSVReader csvReader) {
		try {
			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				this.pushLine(name, line);
			}
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void pushLine(String name, String[] line) {
		List<OnyxStreamingConsumer> consumers = this.getConsumers(name);
		if (consumers == null || consumers.isEmpty()) {
			return;
		}
		for (OnyxStreamingConsumer consumer : consumers) {
			consumer.consumer(line);
		}
	}

	private List<OnyxStreamingConsumer> getConsumers(String name) {
		return consumers.get(name);
	}
}
