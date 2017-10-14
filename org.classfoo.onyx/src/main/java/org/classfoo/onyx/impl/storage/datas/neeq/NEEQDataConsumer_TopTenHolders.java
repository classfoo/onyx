package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;
import org.classfoo.onyx.impl.OnyxUtils;

public class NEEQDataConsumer_TopTenHolders implements OnyxStreamingMessageListener {

	private static final String color = OnyxUtils.getRandomColor();

	private OnyxService onyxService;

	private String kid;

	private OnyxStorageSession session;

	public NEEQDataConsumer_TopTenHolders(OnyxService onyxService, String kid) {
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
		String changeQty = line[0];
		properties.put("变更股份", changeQty);
		String date = line[1];
		properties.put("日期", date);
		String hqzqdm = line[2];
		properties.put("股票代码", hqzqdm);
		String last_quantity = line[3];
		properties.put("最近股份", last_quantity);
		String limitedQuantity = line[4];
		properties.put("限制股份", limitedQuantity);
		String name = line[5];
		properties.put("姓名", name);
		String num = line[6];
		properties.put("排名", num);
		String quantity = line[7];
		properties.put("股份", quantity);
		String ratio = line[8];
		properties.put("占比", ratio);
		String unlimitedQuantity = line[9];
		properties.put("解禁股份", unlimitedQuantity);
		Map<String, Object> entity = consumer.getContext().getEntityByProperty("people", name);
		if(entity == null){
			entity = session.addEntity(this.kid, name, Arrays.asList("股东"), properties);
			consumer.getContext().putEntityByProperty("people", name, entity);
		}
		String targetid = MapUtils.getString(entity, "id");
		String targetname = MapUtils.getString(entity, "name");
		Map<String, Object> target = consumer.getContext().getEntityByProperty("code", hqzqdm);
		String sourceid = MapUtils.getString(target, "id");
		String sourcename = MapUtils.getString(target, "name");
		HashMap<String, Object> linkProperties = new HashMap<String, Object>(1);
		linkProperties.put("color", color);
		session.addLink("股东", sourceid, sourcename, targetid, targetname, linkProperties);
	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}

}
