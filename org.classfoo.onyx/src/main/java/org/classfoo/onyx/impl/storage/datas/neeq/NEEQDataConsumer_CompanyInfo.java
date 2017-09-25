package org.classfoo.onyx.impl.storage.datas.neeq;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessageListener;

public class NEEQDataConsumer_CompanyInfo implements OnyxStreamingMessageListener {

	private OnyxService onyxService;

	private OnyxStorageSession session;

	public NEEQDataConsumer_CompanyInfo(OnyxService onyxService, String kid) {
		this.onyxService = onyxService;
	}

	@Override
	public void onStart(OnyxStreamingConsumer consumer) {
		OnyxStorage storage = this.onyxService.getStorageService().getStorage();
		this.session = storage.openSession();
	}

	@Override
	public void onMessage(OnyxStreamingConsumer consumer, OnyxStreamingMessage message) {
		String[] line = (String[]) message.getBody();
		//hqbjw1（申报买价1）,hqbjw2（申报买价2）,hqbjw3（申报买价3）,hqbjw4（申报买价4）,hqbjw5（申报买价5）,hqbsl1（申报买价数量1）,hqbsl2（申报买价数量2）,hqbsl3（申报买价数量3）,hqbsl4（申报买价数量4）,hqbsl5（申报买价数量5）,hqcjbs,hqcjje(当日累计成交金额(元)),hqcjsl（成交股数）,hqgxsj,hqhycc,hqjrkp（开盘价）,hqjsd1（成交价-前收价）,hqjsd2,hqjsrq（日期）,hqsjw1（申报卖价1）,hqsjw2（申报卖价2）,hqsjw3（申报卖价3）,hqsjw4（申报卖价4）,hqsjw5（申报卖价5）,hqssl1（申报卖单数量1）,hqssl2（申报卖单数量2）,hqssl3（申报卖单数量3）,hqssl4（申报卖单数量4）,hqssl5（申报卖单数量5）,hqsyl1,hqsyl2,hqzd（涨跌）,hqzdcj（最低成交价）,hqzdf（涨跌浮）,hqzgcj（最高成交价）,hqzjcj（成交价）,hqzqdm(股票代码),hqzqjc（公司简称）,hqzrsp（前收）,xxfcbj（是否创新层）,xxhbzl（币种类别）,xxzrlx（交易类型）

	}

	@Override
	public void onShutdown(OnyxStreamingConsumer consumer) {
		this.session.close();
	}
}
