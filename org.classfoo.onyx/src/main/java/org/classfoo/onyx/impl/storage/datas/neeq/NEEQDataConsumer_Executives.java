package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.tinkerpop.gremlin.structure.io.Storage;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.storage.conditions.OnyxEntityCondition;
import org.classfoo.onyx.api.storage.conditions.OnyxEntityConditionListener;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;

public class NEEQDataConsumer_Executives implements OnyxStreamingMessageListener {

	private OnyxService onyxService;

	private String kid;

	private OnyxStorageSession session;

	public NEEQDataConsumer_Executives(OnyxService onyxService, String kid) {
		this.onyxService = onyxService;
		this.kid = kid;
	}

	@Override
	public void onStart(OnyxStreamingConsumer consumer) {
		OnyxStorage storage = this.onyxService.getStorageService().getStorage();
		this.session = storage.openSession();
	}

	@Override
	public void onMessage(OnyxStreamingConsumer consumer, OnyxStreamingMessage message) {
		String[] line = (String[]) message.getBody();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		String age = line[0];
		properties.put("age", age);
		String education = line[1];
		properties.put("education", education);
		String gender = line[2];
		properties.put("gender", gender);
		String hqzqdm = line[3];
		properties.put("hqzqdm", hqzqdm);
		String job = line[4];
		properties.put("job", job);
		String name = line[5];
		properties.put("name", name);
		String salary = line[6];
		properties.put("salary", salary);
		String term = line[7];
		properties.put("term", term);
		String[] jobs = job.split("、");
		Map<String, Object> entity = session.addEntity(this.kid, name, properties);
		String sourceid = MapUtils.getString(entity, "id");
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		OnyxEntityCondition condition = storage.createEntityCondition();
		condition.setLabels("新三板企业");
		condition.addCondition("code", hqzqdm);
		condition.setListener(new OnyxEntityConditionListener() {
			@Override
			public void onMatch(Map<String, Object> entity, OnyxStorageSession session) {
				String targetid = MapUtils.getString(entity, "id");
				for (String job : jobs) {
					session.addLink(job, sourceid, targetid, null);
				}
			}
		});
		storage.addEntityCondition(condition);
	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}

}
