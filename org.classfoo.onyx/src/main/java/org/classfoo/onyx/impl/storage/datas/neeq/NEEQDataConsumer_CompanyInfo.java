package org.classfoo.onyx.impl.storage.datas.neeq;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;

public class NEEQDataConsumer_CompanyInfo implements OnyxStreamingConsumer {

	private OnyxService onyxService;

	public NEEQDataConsumer_CompanyInfo(OnyxService onyxService, String kid) {
		this.onyxService = onyxService;
	}

	@Override
	public void consumer(String[] line) {

	}

}
