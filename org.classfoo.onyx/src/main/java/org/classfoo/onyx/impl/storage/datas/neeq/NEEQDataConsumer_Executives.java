package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;
import org.classfoo.onyx.impl.OnyxUtils;

public class NEEQDataConsumer_Executives implements OnyxStreamingMessageListener {

	private OnyxService onyxService;

	private String kid;

	private OnyxStorageSession session;

	private Map<String, String> colors = new HashMap<String, String>(100);

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
		properties.put("年龄", age);
		String education = line[1];
		properties.put("学历", education);
		String gender = line[2];
		properties.put("性别", gender);
		String hqzqdm = line[3];
		properties.put("股票代码", hqzqdm);
		String job = line[4];
		properties.put("职位", job);
		String name = line[5];
		properties.put("姓名", name);
		String salary = line[6];
		properties.put("在职", salary);
		String term = line[7];
		properties.put("任期", term);
		String[] jobs = StringUtils.split(job, "、,， /／");
		Map<String, Object> entity = session.addEntity(this.kid, name, properties);
		String targetid = MapUtils.getString(entity, "id");
		String targetname = MapUtils.getString(entity, "name");
		Map<String, Object> target = consumer.getContext().getEntityByProperty("code", hqzqdm);
		String sourceid = MapUtils.getString(target, "id");
		String sourcename = MapUtils.getString(target, "name");
		for (String link : jobs) {
			String color = this.getColor(link);
			HashMap<String, Object> linkProperties = new HashMap<String, Object>(1);
			linkProperties.put("color", color);
			this.session.addLink(link, sourceid, sourcename, targetid, targetname, linkProperties);
		}
	}

	private String getColor(String link) {
		String color = this.colors.get(link);
		if (StringUtils.isNotBlank(color)) {
			return color;
		}
		color = OnyxUtils.getRandomColor();
		this.colors.put(link, color);
		return color;
	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}

}
