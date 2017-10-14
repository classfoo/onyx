package org.classfoo.onyx.impl.index;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.classfoo.onyx.api.index.OnyxIndexService;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Component;

/**
 * @see OnyxIndexService
 * @author ClassFoo
 *
 */
@Component
public class OnyxIndexServiceImpl implements OnyxIndexService {

	private TransportClient client;

	private ThreadPoolExecutor threadPool;

	public OnyxIndexServiceImpl() throws UnknownHostException {
		this.client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
				new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		this.threadPool = new ThreadPoolExecutor(8, 8, 1, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(100));
	}

	/**
	 * 创建连接池以执行流程内部步骤
	 * @param tables
	 * @return
	 */
	private ThreadPoolExecutor getThreadPool(int total, final String threadName) {
		return threadPool;
	}

	public void query() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("social-*");
		searchRequest.source(searchSourceBuilder);
		ActionFuture<SearchResponse> searchResponse = client.search(searchRequest);
	}

	@Override
	public void addEntityIndex(Map<String, Object> entity) {
		//BulkRequestBuilder bulkRequest = client.prepareBulk();
		client.prepareIndex("onyx", "entity").setSource(entity).execute();
		//
		//		Runnable runnable = new Runnable() {
		//			@Override
		//			public void run() {
		//			}
		//		};
		//		this.threadPool.submit(runnable);
	}

	@Override
	public List<Map<String, Object>> searchEntity(String name) {
		SearchResponse response = client.prepareSearch("onyx").setTypes("entity").setSearchType(
				SearchType.DFS_QUERY_THEN_FETCH).setQuery(QueryBuilders.matchQuery("name", name)).setFrom(0).setSize(
						32).setExplain(true).get();
		SearchHits hits = response.getHits();
		SearchHit[] hitsArray = hits.getHits();
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(hitsArray.length);
		for (int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> item = hitsArray[i].getSourceAsMap();
			result.add(item);
		}
		return result;
	}
}
