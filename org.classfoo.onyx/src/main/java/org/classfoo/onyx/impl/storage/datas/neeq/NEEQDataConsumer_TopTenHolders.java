package org.classfoo.onyx.impl.storage.datas.neeq;

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
		properties.put("changeQty", changeQty);
		String date = line[1];
		properties.put("date", date);
		String hqzqdm = line[2];
		properties.put("hqzqdm", hqzqdm);
		String last_quantity = line[3];
		properties.put("last_quantity", last_quantity);
		String limitedQuantity = line[4];
		properties.put("limitedQuantity", limitedQuantity);
		String name = line[5];
		properties.put("name", name);
		String num = line[6];
		properties.put("num", num);
		String quantity = line[7];
		properties.put("quantity", quantity);
		String ratio = line[8];
		properties.put("ratio", ratio);
		String unlimitedQuantity = line[9];
		properties.put("unlimitedQuantity", unlimitedQuantity);
		Map<String, Object> entity = session.addEntity(this.kid, name, properties);
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
