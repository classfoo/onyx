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

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.api.index.OnyxIndexThread;
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
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @see OnyxIndexService
 * @author ClassFoo
 *
 */
@Component
public class OnyxIndexServiceImpl implements OnyxIndexService {

	@Autowired
	private OnyxService onyxService;

	private TransportClient client;

	private OnyxIndexThreadImpl indexThread;

	@Override
	public OnyxIndexSession openSession() {
		try {
			if (this.client == null) {
				this.client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			}
			return new OnyxIndexSessionImpl(this.client);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OnyxIndexThread getIndexThread() {
		if (this.indexThread == null) {
			this.indexThread = new OnyxIndexThreadImpl(this.onyxService);
		}
		return this.indexThread;
	}
}
