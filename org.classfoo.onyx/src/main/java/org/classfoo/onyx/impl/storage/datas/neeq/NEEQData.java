package org.classfoo.onyx.impl.storage.datas.neeq;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;
import org.janusgraph.util.system.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

/**
 * NEEQ Testing Data
 * @author ClassFoo
 *
 */
public class NEEQData {

	private OnyxService onyxService;

	public NEEQData(OnyxService onyxService) {
		this.onyxService = onyxService;
	}

	public void initTestData(String kid) {
		OnyxStreamingService streamingService = this.onyxService.getStreamingService();
		streamingService.addConsumer("executives.csv", new NEEQDataConsumer_Executives(this.onyxService, kid));
		streamingService.addConsumer("baseinfo.csv", new NEEQDataConsumer_BaseInfo(this.onyxService, kid));
		streamingService.addConsumer("companyInfo.csv", new NEEQDataConsumer_CompanyInfo(this.onyxService, kid));
		startStreaming("companyInfo.csv");
		startStreaming("baseinfo.csv");
		startStreaming("executives.csv");
	}

	private void startStreaming(String name) {
		OnyxStreamingService streamingService = this.onyxService.getStreamingService();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new InputStreamReader(NEEQData.class.getResourceAsStream(name), "utf-8"));
			streamingService.startCsvStreaming(name, csvReader);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			IOUtils.closeQuietly(csvReader);
		}
	}
}
