package org.classfoo.onyx.impl.storage.datas.neeq;

import java.io.InputStreamReader;
import java.util.UUID;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.streaming.OnyxStreamingConsumer;
import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;
import org.classfoo.onyx.api.streaming.OnyxStreamingProducer;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;
import org.classfoo.onyx.impl.OnyxUtils;
import org.janusgraph.util.system.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;

import au.com.bytecode.opencsv.CSVReader;

/**
 * NEEQ Testing Data
 * @author ClassFoo
 *
 */
public class NEEQData {

	private static final Logger logger = LoggerFactory.getLogger(NEEQData.class);

	private OnyxService onyxService;

	private OnyxStreamingService streamingService;

	public NEEQData(OnyxService onyxService) {
		this.onyxService = onyxService;
		this.streamingService = this.onyxService.getStreamingService();
	}

	public void initTestData(Session session) {
		String kid = OnyxUtils.getRandomUUID("k");
		session.execute("insert into bases (id_,name_,desc_) values(?,?,?)", kid, "新三板知识库",
				"新三板知识库，新三板公司档案，投资人信息，股权关系，交易意向");
		// initialize consumers
		OnyxStreamingConsumer consumer1 = this.streamingService.createConsumer("executives.csv");
		consumer1.registListener(new NEEQDataConsumer_Executives(this.onyxService, kid));
		consumer1.start();
		OnyxStreamingConsumer consumer2 = this.streamingService.createConsumer("baseinfo.csv");
		consumer2.registListener(new NEEQDataConsumer_BaseInfo(this.onyxService, kid));
		consumer2.start();
		OnyxStreamingConsumer consumer3 = this.streamingService.createConsumer("companyInfo.csv");
		consumer3.registListener(new NEEQDataConsumer_CompanyInfo(this.onyxService, kid));
		consumer3.start();
		// initialize producer threads
		ProducerThread thread1 = new ProducerThread("executives.csv", this.onyxService);
		thread1.start();
		ProducerThread thread2 = new ProducerThread("baseinfo.csv", this.onyxService);
		thread2.start();
		ProducerThread thread3 = new ProducerThread("companyInfo.csv", this.onyxService);
		thread3.start();
	}

	private class ProducerThread extends Thread {

		private OnyxService onyxService;

		private String csv;

		public ProducerThread(String csv, OnyxService onyxService) {
			this.csv = csv;
			this.onyxService = onyxService;
		}

		@Override
		public void run() {
			OnyxStreamingService streamingService = this.onyxService.getStreamingService();
			OnyxStreamingProducer producer = streamingService.createProducer(this.csv);
			CSVReader csvReader = null;
			try {
				csvReader = new CSVReader(new InputStreamReader(NEEQData.class.getResourceAsStream(this.csv), "utf-8"));
				String[] line = null;
				long start = System.currentTimeMillis();
				long periodStart = System.currentTimeMillis();
				long count = 0;
				while ((line = csvReader.readNext()) != null) {
					count++;
					if (count == 1) {
						continue;
					}
					OnyxStreamingMessage message = streamingService.createMessage(line);
					producer.send(message);
					if (count % 1000 == 0) {
						long current = System.currentTimeMillis();
						long period = current - periodStart;
						logger.info("流输入‘{}’已处理{}行数据，速度：{}行/秒...", csv, count,
								1000 * 1000 / (period == 0 ? 1 : period));
						periodStart = current;
					}
				}
				long totalPeriod = System.currentTimeMillis() - start;
				logger.info("完成流输入‘{}’处理，总共处理{}行数据，平均速度：{}行/秒...", csv, count,
						count * 1000 / (totalPeriod == 0 ? 1 : totalPeriod));
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				IOUtils.closeQuietly(csvReader);
			}
		}
	}
}
