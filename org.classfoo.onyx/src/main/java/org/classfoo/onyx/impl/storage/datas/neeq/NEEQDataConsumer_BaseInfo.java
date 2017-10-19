package org.classfoo.onyx.impl.storage.datas.neeq;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * 新三板：公司基本信息
 * @author ClassFoo
 *
 */
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
		String industry = OnyxUtils.removeBlank(line[7]);
		properties.put("行业", industry);
		String legalRepresentative = OnyxUtils.removeBlank(line[8]);
		properties.put("法人代表", legalRepresentative);
		String listingDate = line[9];
		properties.put("挂牌时间", listingDate);
		String name = OnyxUtils.removeBlank(line[10]);
		properties.put("名称", name);
		String phone = line[11];
		properties.put("电话", phone);
		String postcode = line[12];
		properties.put("邮编", postcode);
		String secretaries = OnyxUtils.removeBlank(line[13]);
		properties.put("董事会秘书", secretaries);
		String shortname = OnyxUtils.removeBlank(line[14]);
		properties.put("简称", shortname);
		String totalStockEquity = line[15];
		properties.put("总股本", totalStockEquity);
		String transferMode = line[16];
		properties.put("交易方式", transferMode);
		String website = line[17];
		properties.put("网站", website);
		String[] industries = StringUtils.split(industry, "、,， /／");
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("挂牌公司");
		labels.add(area);
		if(industries != null){
			labels.addAll(Arrays.asList(industries));
		}
		Map<String, Object> company = session.addEntity(this.kid, shortname, labels, properties);
		consumer.getContext().putEntityByProperty("code", code, company);
		//券商
		this.addBroker(broker, company, consumer);
		//董事长秘书
		this.addSecretaries(secretaries, shortname, code, company, consumer);
		//法人代表
		this.addLegalRepresentative(legalRepresentative, shortname, code, company, consumer);
	}

	/**
	 * 添加主办券商信息
	 * @param broker
	 * @param company
	 * @param consumer
	 */
	private void addBroker(String broker, Map<String, Object> company, OnyxStreamingConsumer consumer) {
		Map<String, Object> brokerEntity = consumer.getContext().getEntityByProperty("company", broker);
		if (brokerEntity == null) {
			brokerEntity = session.addEntity(this.kid, broker, Arrays.asList("券商"), null);
			consumer.getContext().putEntityByProperty("company", broker, brokerEntity);
		}
		else {
			String eid = MapUtils.getString(brokerEntity, "id");
			session.addEntityLabels(eid, Arrays.asList("券商"));
		}
		this.addLink("主办券商", ZBQS_COLOR, company, brokerEntity);
	}

	/**
	 * 添加法人代表信息
	 * @param legalRepresentative
	 * @param companyName
	 * @param code
	 * @param company
	 * @param consumer
	 */
	private void addLegalRepresentative(String legalRepresentative, String companyName, String code,
			Map<String, Object> company, OnyxStreamingConsumer consumer) {
		if (legalRepresentative == null) {
			return;
		}
		Map<String, Object> legalRepresentativeEntity = consumer.getContext().getEntityByProperty("people",
				legalRepresentative + ":" + code);
		if (legalRepresentativeEntity == null) {
			HashMap<String, Object> legalRepresentativeEntityProperties = new HashMap<String, Object>();
			legalRepresentativeEntityProperties.put("挂牌公司", companyName);
			legalRepresentativeEntityProperties.put("股票代码", code);
			legalRepresentativeEntity = session.addEntity(this.kid, legalRepresentative,
					Arrays.asList(companyName, "法人代表"), legalRepresentativeEntityProperties);
			consumer.getContext().putEntityByProperty("people", legalRepresentative + ":" + code,
					legalRepresentativeEntity);
		}
		else {
			String eid = MapUtils.getString(legalRepresentativeEntity, "id");
			session.addEntityLabels(eid, Arrays.asList(companyName, "法人代表"));
			HashMap<String, Object> legalRepresentativeEntityProperties = new HashMap<String, Object>();
			legalRepresentativeEntityProperties.put("挂牌公司", companyName);
			legalRepresentativeEntityProperties.put("股票代码", code);
			session.addEntityProperties(eid, legalRepresentativeEntityProperties);
		}
		this.addLink("法人代表", ZBQS_COLOR, company, legalRepresentativeEntity);
	}

	/**
	 * 添加董事会秘书信息
	 * @param secretaries
	 * @param companyName
	 * @param code
	 * @param company
	 * @param consumer
	 */
	private void addSecretaries(String secretaries, String companyName, String code, Map<String, Object> company,
			OnyxStreamingConsumer consumer) {
		if (StringUtils.isBlank(secretaries)) {
			return;
		}
		Map<String, Object> secretariesEntity = consumer.getContext().getEntityByProperty("people",
				secretaries + ":" + code);
		if (secretariesEntity == null) {
			HashMap<String, Object> secretariesEntityProperties = new HashMap<String, Object>();
			secretariesEntityProperties.put("挂牌公司", companyName);
			secretariesEntityProperties.put("股票代码", code);
			secretariesEntity = session.addEntity(this.kid, secretaries, Arrays.asList(companyName, "董事会秘书"),
					secretariesEntityProperties);
			consumer.getContext().putEntityByProperty("people", secretaries + ":" + code, secretariesEntity);
		}
		else {
			String eid = MapUtils.getString(secretariesEntity, "id");
			session.addEntityLabels(eid, Arrays.asList(companyName, "董事会秘书"));
			HashMap<String, Object> secretariesEntityProperties = new HashMap<String, Object>();
			secretariesEntityProperties.put("挂牌公司", companyName);
			secretariesEntityProperties.put("股票代码", code);
			session.addEntityProperties(eid, secretariesEntityProperties);
		}
		this.addLink("董事会秘书", ZBQS_COLOR, company, secretariesEntity);
	}

	/**
	 * 添加关联
	 * @param name
	 * @param color
	 * @param entity
	 * @param brokerEntity
	 */
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
