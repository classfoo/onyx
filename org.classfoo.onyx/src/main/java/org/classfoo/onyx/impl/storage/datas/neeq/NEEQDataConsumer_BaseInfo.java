package org.classfoo.onyx.impl.storage.datas.neeq;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddEntity;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBase;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;

public class NEEQDataConsumer_BaseInfo implements OnyxStreamingConsumer {

	private OnyxService onyxService;

	private String kid;

	public NEEQDataConsumer_BaseInfo(OnyxService onyxService, String kid) {
		this.onyxService = onyxService;
		this.kid = kid;
	}

	@Override
	public void consumer(String[] line) {
		String address = line[0];
		String area = line[1];
		String broker = line[2];
		String code = line[3];
		String email = line[4];
		String englishName = line[5];
		String fax = line[6];
		String industry = line[7];
		String legalRepresentative = line[8];
		String listingDate = line[9];
		String name = line[10];
		String phone = line[11];
		String postcode = line[12];
		String secretaries = line[13];
		String shortname = line[14];
		String totalStockEquity = line[15];
		String transferMode = line[16];
		String website = line[17];
		OnyxOperateAddEntity addEntity = onyxService.createOperate(OnyxOperateAddEntity.class);
		addEntity.setKnowledgeBase(kid);
		addEntity.setName(name);
		addEntity.commit();
	}

}
