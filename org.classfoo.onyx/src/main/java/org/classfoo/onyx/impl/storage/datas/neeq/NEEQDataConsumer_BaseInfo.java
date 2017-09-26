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

public class NEEQDataConsumer_BaseInfo implements OnyxStreamingMessageListener {

	private OnyxService onyxService;

	private String kid;

	private OnyxStorageSession session;

	public NEEQDataConsumer_BaseInfo(OnyxService onyxService, String kid) {
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
		String address = line[0];
		properties.put("address", address);
		String area = line[1];
		properties.put("area", area);
		String broker = line[2];
		properties.put("broker", broker);
		String code = line[3];
		properties.put("code", code);
		String email = line[4];
		properties.put("email", email);
		String englishName = line[5];
		properties.put("englishName", englishName);
		String fax = line[6];
		properties.put("fax", fax);
		String industry = line[7];
		properties.put("industry", industry);
		String legalRepresentative = line[8];
		properties.put("legalRepresentative", legalRepresentative);
		String listingDate = line[9];
		properties.put("listingDate", listingDate);
		String name = line[10];
		properties.put("name", name);
		String phone = line[11];
		properties.put("phone", phone);
		String postcode = line[12];
		properties.put("postcode", postcode);
		String secretaries = line[13];
		properties.put("secretaries", secretaries);
		String shortname = line[14];
		properties.put("shortname", shortname);
		String totalStockEquity = line[15];
		properties.put("totalStockEquity", totalStockEquity);
		String transferMode = line[16];
		properties.put("transferMode", transferMode);
		String website = line[17];
		properties.put("website", website);
		Map<String, Object> entity = session.addEntity(this.kid, shortname, properties);
		String entityId = MapUtils.getString(entity, "id");
		consumer.getContext().putEntityIdByProperty("code", code, entityId);
	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}
}
