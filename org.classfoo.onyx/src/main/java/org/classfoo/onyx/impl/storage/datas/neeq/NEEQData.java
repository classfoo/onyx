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
		String uuid = OnyxUtils.getCompressedUUID();
		OnyxStreamingConsumer consumer = this.streamingService.createConsumer(uuid);
		consumer.registListener("executives.csv", new NEEQDataConsumer_Executives(this.onyxService, kid));
		consumer.registListener("baseinfo.csv", new NEEQDataConsumer_BaseInfo(this.onyxService, kid));
		consumer.registListener("topTenHolders.csv", new NEEQDataConsumer_TopTenHolders(this.onyxService, kid));
		consumer.start();
		// initialize producer threads
		ProducerThread thread = new ProducerThread(this.onyxService, uuid, "baseinfo.csv", "executives.csv",
				"topTenHolders.csv");
		thread.setName("NEEQProducer");
		thread.start();
	}

	private class ProducerThread extends Thread {

		private OnyxService onyxService;

		private String[] csvs;

		private String uuid;

		public ProducerThread(OnyxService onyxService, String uuid, String... csvs) {
			this.onyxService = onyxService;
			this.uuid = uuid;
			this.csvs = csvs;
		}

		@Override
		public void run() {
			OnyxStreamingService streamingService = this.onyxService.getStreamingService();
			OnyxStreamingProducer producer = streamingService.createProducer(this.uuid);
			for (String csv : this.csvs) {
				this.runCsv(csv, producer);
			}
		}

		private void runCsv(String csv, OnyxStreamingProducer producer) {
			CSVReader csvReader = null;
			try {
				csvReader = new CSVReader(new InputStreamReader(NEEQData.class.getResourceAsStream(csv), "utf-8"));
				String[] line = null;
				long start = System.currentTimeMillis();
				long periodStart = System.currentTimeMillis();
				long count = 0;
				while ((line = csvReader.readNext()) != null) {
					count++;
					if (count == 1) {
						continue;
					}
					OnyxStreamingMessage message = streamingService.createMessage(csv, line);
					producer.send(message);
					if (count % 1000 == 0) {
						long current = System.currentTimeMillis();
						long period = current - periodStart;
						//logger.info("流‘{}’已输入{}行数据，耗时：{}秒，速度：{}行/秒...", csv, count, ((double) period) / 1000,
						//		1000 * 1000 / (period == 0 ? 1 : period));
						periodStart = current;
					}
				}
				long totalPeriod = System.currentTimeMillis() - start;
				logger.info("完成流‘{}’的输入处理，总共输入{}行数据，平均速度：{}行/秒...", csv, count,
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
