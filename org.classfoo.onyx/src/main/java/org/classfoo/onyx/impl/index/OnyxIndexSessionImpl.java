package org.classfoo.onyx.impl.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.impl.OnyxUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class OnyxIndexSessionImpl implements OnyxIndexSession {

	private TransportClient client;

	public OnyxIndexSessionImpl(TransportClient client) {
		this.client = client;
	}

	@Override
	public void addIndex(String index, String type, String id, Map<String, Object> object) {
		client.prepareIndex(index, type, id).setSource(object).execute();
	}

	@Override
	public void mergeIndex(String index, String type, String id, Map<String, Object> object) {
		HashMap<String, Object> item = new HashMap<String, Object>(2);
		item.put("name", id);
		item.put("objects", Arrays.asList(object));
		Script script = new Script(
				"if(!ctx._source.objects.contains(params.object))ctx._source.objects.add(params.object)");
		HashMap<String, Object> scriptParams = new HashMap<String, Object>(1);
		scriptParams.put("object", object);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).script(script).scriptParams(
				scriptParams).upsert(item);
		try {
			client.update(updateRequest).get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void updateIndex(String index, String type, String id, Map<String, Object> object) {
		client.prepareUpdate(index, type, id).setDoc(object).get();
	}

	@Override
	public void upsertIndex(String index, String type, String id, Map<String, Object> object) {
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(object);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(object).upsert(indexRequest).retryOnConflict(5);
		try {
			client.update(updateRequest).get();
		}
		catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addEntityIndex(Map<String, Object> entity) {
		String id = MapUtils.getString(entity, "id");
		this.addIndex("onyx", "entity", id, entity);
	}

	@Override
	public List<Map<String, Object>> searchEntities(String name) {
		SearchRequestBuilder builder = client.prepareSearch("onyx");
		builder.setTypes("entity");
		builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if (StringUtils.isNotBlank(name)) {
			builder.setQuery(QueryBuilders.matchQuery("name", name));
		}
		builder.setFrom(0);
		builder.setSize(32);
		builder.setExplain(true);
		SearchResponse response = builder.get();
		SearchHits hits = response.getHits();
		SearchHit[] hitsArray = hits.getHits();
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(hitsArray.length);
		for (int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> item = hitsArray[i].getSourceAsMap();
			item.put("color", OnyxUtils.getRandomColor());
			result.add(item);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> searchBaseEntities(String kid, String name) {
		SearchRequestBuilder builder = client.prepareSearch("onyx");
		builder.setTypes("entity");
		builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if (StringUtils.isNotBlank(name)) {
			builder.setQuery(QueryBuilders.matchQuery("name", name));
		}
		if (StringUtils.isNotBlank(kid)) {
			builder.setQuery(QueryBuilders.matchQuery("kid", kid));
		}
		builder.setFrom(0);
		builder.setSize(32);
		builder.setExplain(true);
		SearchResponse response = builder.get();
		SearchHits hits = response.getHits();
		SearchHit[] hitsArray = hits.getHits();
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(hitsArray.length);
		for (int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> item = hitsArray[i].getSourceAsMap();
			item.put("color", OnyxUtils.getRandomColor());
			result.add(item);
		}
		return result;
	}

	@Override
	public void addLabelIndex(Map<String, Object> json) {
	}

	@Override
	public List<Map<String, Object>> searchLabels(String name) {
		SearchRequestBuilder builder = client.prepareSearch("onyx");
		builder.setTypes("label");
		builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if (StringUtils.isNotBlank(name)) {
			builder.setQuery(QueryBuilders.matchQuery("name", name));
		}
		builder.setFrom(0);
		builder.setSize(32);
		builder.setExplain(true);
		SearchResponse response = builder.get();
		SearchHits hits = response.getHits();
		SearchHit[] hitsArray = hits.getHits();
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(hitsArray.length);
		for (int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> item = hitsArray[i].getSourceAsMap();
			item.put("color", OnyxUtils.getRandomColor());
			result.add(item);
		}
		return result;
	}

	@Override
	public void clearIndexes() {
		IndicesExistsRequest inExistsRequest = new IndicesExistsRequest("onyx");
		client.admin().indices().exists(inExistsRequest).actionGet();
		//		DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(QueryBuilders.matchAllQuery()).source("onyx",
		//				"global").get();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
