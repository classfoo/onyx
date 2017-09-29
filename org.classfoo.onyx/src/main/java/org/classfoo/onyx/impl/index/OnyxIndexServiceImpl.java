package org.classfoo.onyx.impl.index;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.classfoo.onyx.api.index.OnyxIndexService;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
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

	public OnyxIndexServiceImpl() throws UnknownHostException {
//		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
//				new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//		searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));
//		SearchRequest searchRequest = new SearchRequest();
//		searchRequest.indices("social-*");
//		searchRequest.source(searchSourceBuilder);
//		ActionFuture<SearchResponse> searchResponse = client.search(searchRequest);
	}
}
