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

public class NEEQDataConsumer_BaseInfo implements OnyxStreamingMessageListener {

	private static final String ZBQS_COLOR = OnyxUtils.getRandomColor();

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
		properties.put("地址", address);
		String area = line[1];
		properties.put("区域", area);
		String broker = line[2];
		properties.put("主办券商", broker);
		String code = line[3];
		properties.put("股票代码", code);
		String email = line[4];
		properties.put("邮件", email);
		String englishName = line[5];
		properties.put("英文名", englishName);
		String fax = line[6];
		properties.put("传真", fax);
		String industry = line[7];
		properties.put("行业", industry);
		String legalRepresentative = line[8];
		properties.put("法人代表", legalRepresentative);
		String listingDate = line[9];
		properties.put("挂牌时间", listingDate);
		String name = line[10];
		properties.put("名称", name);
		String phone = line[11];
		properties.put("电话", phone);
		String postcode = line[12];
		properties.put("邮编", postcode);
		String secretaries = line[13];
		properties.put("董事长秘书", secretaries);
		String shortname = line[14];
		properties.put("简称", shortname);
		String totalStockEquity = line[15];
		properties.put("总股本", totalStockEquity);
		String transferMode = line[16];
		properties.put("交易方式", transferMode);
		String website = line[17];
		properties.put("网站", website);
		Map<String, Object> entity = session.addEntity(this.kid, shortname, Arrays.asList("挂牌公司"), properties);
		consumer.getContext().putEntityByProperty("code", code, entity);
		//券商
		Map<String, Object> brokerEntity = consumer.getContext().getEntityByProperty("company", broker);
		if (brokerEntity == null) {
			brokerEntity = session.addEntity(this.kid, broker, Arrays.asList("券商"), null);
			consumer.getContext().putEntityByProperty("company", broker, brokerEntity);
		}
		this.addLink("主办券商", ZBQS_COLOR, entity, brokerEntity);
		//董事长秘书
		Map<String, Object> secretariesEntity = consumer.getContext().getEntityByProperty("people", secretaries);
		if (secretariesEntity == null) {
			secretariesEntity = session.addEntity(this.kid, secretaries, Arrays.asList("董事长秘书"), properties);
			consumer.getContext().putEntityByProperty("people", secretaries, secretariesEntity);
		}
		this.addLink("董事长秘书", ZBQS_COLOR, entity, secretariesEntity);
		//法人代表
		Map<String, Object> legalRepresentativeEntity = consumer.getContext().getEntityByProperty("people",
				legalRepresentative);
		if (legalRepresentativeEntity == null) {
			legalRepresentativeEntity = session.addEntity(this.kid, legalRepresentative, Arrays.asList("法人代表"),
					properties);
			consumer.getContext().putEntityByProperty("people", legalRepresentative, legalRepresentativeEntity);
		}
		this.addLink("法人代表", ZBQS_COLOR, entity, legalRepresentativeEntity);
	}

	private void addLink(String name, String color, Map<String, Object> entity, Map<String, Object> brokerEntity) {
		String sourceid = MapUtils.getString(entity, "id");
		String sourcename = MapUtils.getString(entity, "name");
		String targetid = MapUtils.getString(brokerEntity, "id");
		String targetname = MapUtils.getString(brokerEntity, "name");
		Map<String, Object> linkProperties = new HashMap<String, Object>(1);
		linkProperties.put("color", color);
		this.session.addLink(name, sourceid, sourcename, targetid, targetname, linkProperties);
	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}
}
