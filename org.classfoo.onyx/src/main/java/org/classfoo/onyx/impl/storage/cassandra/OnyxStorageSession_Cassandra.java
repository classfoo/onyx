package org.classfoo.onyx.impl.storage.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexThread;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * @see OnyxStorageSession
 * @author ClassFoo
 *
 */
public class OnyxStorageSession_Cassandra implements OnyxStorageSession {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStorageSession_Cassandra.class);

	private OnyxService onyxService;

	private Session session;

	private OnyxStorage storage;

	private Map<String, PreparedStatement> statements = new ConcurrentHashMap<String, PreparedStatement>();

	private BatchStatement batch;

	public OnyxStorageSession_Cassandra(OnyxService onyxService, OnyxStorage storage, Session session) {
		this.onyxService = onyxService;
		this.storage = storage;
		this.session = session;
	}

	@Override
	public List<Map<String, Object>> queryBases() {
		ResultSet value = this.executeQuery("select * from bases");
		return convertToList(value);
	}

	@Override
	public Map<String, Object> queryBase(String id) {
		ResultSet value = this.executeQuery("select * from bases where id_=?", id);
		return convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryEntities() {
		ResultSet value = this.executeQuery("select * from entities LIMIT 32");
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryBaseEntities(String kid) {
		ResultSet value = this.executeQuery("select * from base_entity where kid_=? LIMIT 32", kid);
		return convertToList(value);
	}

	@Override
	public Map<String, Object> queryEntity(String eid) {
		ResultSet value = this.executeQuery("select * from entities where id_=?", eid);
		return convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryEntityModifies(String eid) {
		ResultSet value = this.executeQuery("select * from entities where id_=?", eid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryEntityEvents(String eid) {
		ResultSet value = this.executeQuery("select * from entity_event where id_=?", eid);
		return convertToList(value);
	}

	@Override
	public Map<String, Object> queryBaseLabel(String kid, String name) {
		ResultSet value = this.executeQuery("select * from labels where kid_=? and name_=?", kid, name);
		return this.convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryBaseLabels(String kid) {
		ResultSet value = this.executeQuery("select * from base_label where kid_=?", kid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryLabelModifies(String lid) {
		ResultSet value = this.executeQuery("select * from labels where id_=? ALLOW FILTERING", lid);
		return this.convertToList(value);
	}

	@Override
	public Map<String, Object> queryMaterial(String mid) {
		ResultSet value = this.executeQuery("select * from materials where id_=?", mid);
		return convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryMaterials(String kid) {
		ResultSet value = this.executeQuery("select * from materials");
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryGraphs() {
		ResultSet value = this.executeQuery("select * from graphs limit 32");
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryBaseGraphs(String kid) {
		ResultSet value = this.executeQuery("select * from base_graph where kid_=?", kid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryLinks(String eid) {
		ResultSet sourceValue = this.executeQuery("select * from links_source where source_=?", eid);
		List<Map<String, Object>> sources = this.convertToList(sourceValue);
		ResultSet targetValue = this.executeQuery("select * from links_target where target_=?", eid);
		List<Map<String, Object>> targets = this.convertToList(targetValue);
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result.addAll(sources);
		result.addAll(targets);
		return result;
	}

	@Override
	public List<Map<String, Object>> queryLinkNames(String eid) {
		ResultSet sourceValue = this.executeQuery("select name_ from links_source where source_=?", eid);
		ArrayList<Map<String, Object>> linknames = new ArrayList<Map<String, Object>>(20);
		Set<String> ignores = new HashSet<String>();
		addToLinkNames(sourceValue, linknames, "out", ignores);
		ResultSet targetValue = this.executeQuery("select name_ from links_target where target_=?", eid);
		addToLinkNames(targetValue, linknames, "in", ignores);
		return linknames;
	}

	private void addToLinkNames(ResultSet value, List<Map<String, Object>> linknames, String type,
			Set<String> ignores) {
		Iterator<Row> it = value.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			String name = row.getString(0);
			if (ignores.contains(name)) {
				continue;
			}
			ignores.add(name);
			Map<String, Object> item = new HashMap<String, Object>(2);
			item.put("name", name);
			item.put("type", type);
			linknames.add(item);
		}
	}

	@Override
	public Map<String, Object> queryLinkNodes(String eid, Map<String, Object> options) {
		String name = MapUtils.getString(options, "name");
		String type = MapUtils.getString(options, "type");
		if (StringUtils.isBlank(type)) {//in out
			if (StringUtils.isBlank(name)) {
				return queryLinkNodesInOutByEid(eid);
			}
			else {
				return queryLinkNodesInOutByName(eid, name);
			}
		}
		if ("in".equals(type)) {
			if (StringUtils.isBlank(name)) {
				return queryLinkNodesInByEid(eid);
			}
			else {
				return queryLinkNodesInByName(eid, name);
			}
		}
		else {
			if (StringUtils.isBlank(name)) {
				return this.queryLinkNodesOutByEid(eid);
			}
			else {
				return this.queryLinkNodesOutByName(eid, name);
			}
		}
	}

	private Map<String, Object> queryLinkNodesInOutByEid(String eid) {
		return this.queryLinkNodesInOutBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_target where target_=? limit 20",
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_source where source_=? limit 20",
				eid);
	}

	private Map<String, Object> queryLinkNodesInOutByName(String eid, String name) {
		return this.queryLinkNodesInOutBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_target where target_=? and name = ?  limit 20",
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_source where source_=? and name = ? limit 20",
				eid, name);
	}

	private Map<String, Object> queryLinkNodesInOutBySql(String sql1, String sql2, Object... params) {
		List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>(10);
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>(10);
		ResultSet value1 = this.executeQuery(sql1, params);
		this.convertLinkNodesIn(value1, entities, links);
		ResultSet value2 = this.executeQuery(sql2, params);
		this.convertLinkNodesOut(value2, entities, links);
		HashMap<String, Object> result = new HashMap<String, Object>(2);
		result.put("entities", entities);
		result.put("links", links);
		return result;
	}

	private Map<String, Object> queryLinkNodesInByEid(String eid) {
		return this.queryLinkNodesInBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_target where target_=? limit 20",
				eid);
	}

	private Map<String, Object> queryLinkNodesInByName(String eid, String name) {
		return this.queryLinkNodesInBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_target where target_=? and name_=? limit 20",
				eid, name);
	}

	private Map<String, Object> queryLinkNodesInBySql(String sql, Object... params) {
		ResultSet sourceValue = this.executeQuery(sql, params);
		return convertLinkNodesIn(sourceValue);
	}

	private Map<String, Object> convertLinkNodesIn(ResultSet sourceValue) {
		List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>(10);
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>(10);
		convertLinkNodesIn(sourceValue, entities, links);
		HashMap<String, Object> result = new HashMap<String, Object>(2);
		result.put("entities", entities);
		result.put("links", links);
		return result;
	}

	private void convertLinkNodesIn(ResultSet sourceValue, List<Map<String, Object>> entities,
			List<Map<String, Object>> links) {
		Iterator<Row> it = sourceValue.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			String linkid = row.getString(0);
			String linkname = row.getString(1);
			String sourceid = row.getString(2);
			String sourcename = row.getString(3);
			String targetid = row.getString(4);
			String targetname = row.getString(5);
			Map<String, String> properties = row.getMap(6, String.class, String.class);
			HashMap<String, Object> entity = new HashMap<String, Object>(2);
			entity.put("id", sourceid);
			entity.put("name", sourcename);
			entities.add(entity);
			HashMap<String, Object> link = new HashMap<String, Object>(5);
			link.put("id", linkid);
			link.put("name", linkname);
			link.put("source", sourceid);
			link.put("target", targetid);
			link.put("properties", properties);
			links.add(link);
		}
	}

	private Map<String, Object> queryLinkNodesOutByEid(String eid) {
		return queryLinkNodesOutBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_source where source_=? limit 20",
				eid);
	}

	private Map<String, Object> queryLinkNodesOutByName(String eid, String name) {
		return queryLinkNodesOutBySql(
				"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_source where source_=? and name_=? limit 20",
				eid, name);
	}

	private Map<String, Object> queryLinkNodesOutBySql(String sql, Object... params) {
		ResultSet sourceValue = this.executeQuery(sql, params);
		return convertLinkNodesOut(sourceValue);
	}

	private Map<String, Object> convertLinkNodesOut(ResultSet sourceValue) {
		List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>(10);
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>(10);
		convertLinkNodesOut(sourceValue, entities, links);
		HashMap<String, Object> result = new HashMap<String, Object>(2);
		result.put("entities", entities);
		result.put("links", links);
		return result;
	}

	private void convertLinkNodesOut(ResultSet sourceValue, List<Map<String, Object>> entities,
			List<Map<String, Object>> links) {
		Iterator<Row> it = sourceValue.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			String linkid = row.getString(0);
			String linkname = row.getString(1);
			String sourceid = row.getString(2);
			String sourcename = row.getString(3);
			String targetid = row.getString(4);
			String targetname = row.getString(5);
			Map<String, String> properties = row.getMap(6, String.class, String.class);
			HashMap<String, Object> entity = new HashMap<String, Object>(2);
			entity.put("id", targetid);
			entity.put("name", targetname);
			entities.add(entity);
			HashMap<String, Object> link = new HashMap<String, Object>(5);
			link.put("id", linkid);
			link.put("name", linkname);
			link.put("source", sourceid);
			link.put("target", targetid);
			link.put("properties", properties);
			links.add(link);
		}
	}

	private List<Map<String, Object>> convertToList(ResultSet value) {
		List<Row> results = value.all();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>(results.size());
		for (Row result : results) {
			Map<String, Object> item = this.convertToMap(result);
			list.add(item);
		}
		return list;
	}

	private Map<String, Object> convertToMap(ResultSet value) {
		Row row = value.one();
		return this.convertToMap(row);
	}

	private Map<String, Object> convertToMap(Row result) {
		if (result == null) {
			return Collections.emptyMap();
		}
		ColumnDefinitions columns = result.getColumnDefinitions();
		Iterator<Definition> iterator = columns.iterator();
		HashMap<String, Object> item = new HashMap<String, Object>(columns.size());
		while (iterator.hasNext()) {
			Definition next = iterator.next();
			String name = next.getName();
			Object value = result.getObject(name);
			if (name.charAt(name.length() - 1) == '_') {
				item.put(name.substring(0, name.length() - 1), value);
			}
			else {
				item.put(name, value);
			}
		}
		return item;
	}

	@Override
	public Map<String, Object> addBase(String kid, String name, String desc) {
		this.executeUpdate("insert into bases (id_,name_,desc_) values(?,?,?)", kid, name, desc);
		HashMap<String, Object> result = new HashMap<String, Object>(3);
		result.put("id", kid);
		result.put("name", name);
		result.put("desc", desc);
		return result;
	}

	@Override
	public void addLabel(String kid, String lid, String name, Map<String, Object> properties) {
		this.executeUpdate("insert into labels (kid_,id_,name_,properties_) values(?,?,?,?)", kid, lid, name,
				properties);
	}

	@Override
	public void updateBaseName(String id, String newname) {

	}

	@Override
	public void updateBaseCaption(String id, String newcaption) {

	}

	@Override
	public void updateBaseProperties(String id, Map<String, Object> newproperties) {

	}

	@Override
	public Map<String, Object> createLabel(String kid, String labelName, List<String> parents, List<String> links,
			List<String> properties) {
		String lid = OnyxUtils.getRandomUUID("l");
		ResultSet value = this.executeUpdate(
				"insert into labels (id,kid,name,parents,links,properties) values (?,?,?,?,?,?)", lid, kid, labelName,
				parents, links, properties);
		return convertToMap(value);
	}

	@Override
	public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
			List<String> links, List<String> properties) {
		ResultSet value = this.executeUpdate(
				"update labels set name=?,parents=?,links=?,properties=? where kid=? and id=?", labelName, parents,
				links, properties, kid, lid);
		return convertToMap(value);
	}

	@Override
	public Map<String, Object> addEntity(String kid, String name, List<String> labels, Map<String, Object> properties) {
		String eid = OnyxUtils.getRandomUUID("e");
		this.executeUpdate("insert into entities (kid_,id_,name_,labels_, properties_) values(?,?,?,?,?)", kid, eid,
				name, labels, properties);
		this.executeUpdate("insert into base_entity (kid_,id_,name_) values(?,?,?)", kid, eid, name);
		HashMap<String, Object> entity = new HashMap<String, Object>(6);
		entity.put("type", "entity");
		entity.put("id", eid);
		entity.put("kid", kid);
		entity.put("name", name);
		entity.put("labels", labels);
		entity.put("properties", properties);
		this.addEntityIndex(eid, name, entity);
		return entity;
	}

	private void addEntityIndex(String id, String name, Map<String, Object> entity) {
		OnyxIndexService indexService = this.onyxService.getIndexService();
		OnyxIndexThread indexThread = indexService.getIndexThread();
		indexThread.addIndex(OnyxIndexThread.INSERT, "onyx", "entity", id, entity);
		indexThread.addIndex(OnyxIndexThread.MERGE, "global", "entity", name, entity);
	}

	@Override
	public Map<String, Object> addEntityLabels(String id, List<String> labels) {
		this.executeUpdate("update entities set labels_=labels_+? where id_=?", labels, id);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("eid", id);
		result.put("labels", labels);
		return result;
	}

	@Override
	public Map<String, Object> addEntityProperties(String id, Map<String, Object> properties) {
		this.executeUpdate("update entities set properties_=properties_+? where id_=?", properties, id);
		return properties;
	}

	@Override
	public Map<String, Object> updateEntity(String kid, String eid, List<String> labels,
			Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> updateEntity(String kid, String name, List<Map<String, Object>> modifies) {
		String eid = OnyxUtils.getRandomUUID("e");
		ResultSet value = this.executeUpdate(
				"insert into entities (kid_,id_,name_,event_,order_,property_,operate_,key_,value_,user_) values(?,?,?,now(),1,'name','add',?,?,?)",
				kid, eid, name, name, null, "admin");
		return convertToMap(value);
	}

	@Override
	public void addEvent(String eid, String type, String name, String time, Map<String, Object> properties) {
		String eventid = OnyxUtils.getRandomUUID("t");
		this.executeUpdate("insert into events (id_,eid_ ,time_,name_,type_,properties_) values(?,?,?,?,?,?)", eventid,
				eid, time, name, type, properties);
		this.executeUpdate("insert into entity_event (id_, time_,name_,type_, eid_) values(?,?,?,?,?)", eid, time, name,
				type, eventid);
	}

	@Override
	public Map<String, Object> saveLabelModifies(String kid, String lid, String labelName,
			List<Map<String, Object>> modifies) {
		int i = 1;
		for (Map<String, Object> modify : modifies) {
			String key = OnyxUtils.readJson(modify, "key", String.class);
			String operate = OnyxUtils.readJson(modify, "operate", String.class);
			String name = OnyxUtils.readJson(modify, "name", String.class);
			String parent = OnyxUtils.readJson(modify, "parent", String.class);
			String ptype = OnyxUtils.readJson(modify, "ptype", String.class);
			String pname = OnyxUtils.readJson(modify, "pname", String.class);
			String poptions = OnyxUtils.readJson(modify, "poptions", String.class);
			this.executeUpdate(
					"insert into labels (id_,event_,order_,key_,operate_,name_,parent_,ptype_,pname_,poptions_,kid_,user_) values (?,now(),?,?,?,?,?,?,?,?,?,?)",
					lid, i, key, operate, name, parent, ptype, pname, poptions, kid, "admin");
		}
		return null;
	}

	@Override
	public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties) {
		String mid = OnyxUtils.getRandomUUID("m");
		ResultSet value = this.executeUpdate(
				"insert into materials (id_,kid_,name_,desc_,properties_) values (?,?,?,?,?)", mid, kid, name, desc,
				properties);
		HashMap<String, Object> result = new HashMap<String, Object>(4);
		result.put("id", mid);
		result.put("name", name);
		result.put("desc", desc);
		result.put("kid", kid);
		return result;
	}

	@Override
	public void addLink(String name, String sourceid, String sourcename, String targetid, String targetname,
			Map<String, Object> properties) {
		String linkid = OnyxUtils.getRandomUUID("r");
		this.executeUpdate("insert into links (id_,source_,target_,name_,properties_) values (?,?,?,?,?)", linkid,
				sourceid, targetid, name, properties);
		this.executeUpdate(
				"insert into links_source (id_,source_,sourcename_,target_,targetname_,name_,properties_) values (?,?,?,?,?,?,?)",
				linkid, sourceid, sourcename, targetid, targetname, name, properties);
		this.executeUpdate(
				"insert into links_target (id_,source_,sourcename_,target_,targetname_,name_,properties_) values (?,?,?,?,?,?,?)",
				linkid, sourceid, sourcename, targetid, targetname, name, properties);
	}

	@Override
	public void addGraph(String name, String kid, String content, Map<String, Object> properties) {
		String graphid = OnyxUtils.getRandomUUID("g");
		this.executeUpdate("insert into graphs (id_,kid_,name_,content_,properties_) values (?,?,?,?,?)", graphid, kid,
				name, content, properties);
		this.executeUpdate("insert into base_graph (kid_,id_,name_) values (?,?,?)", kid, graphid, name);
	}

	@Override
	public void beginBatch() {
		this.batch = new BatchStatement(BatchStatement.Type.UNLOGGED);
	}

	private ResultSet executeQuery(String sql, Object... params) {
		return this.session.execute(sql, params);
	}

	private ResultSet executeUpdate(String sql, Object... params) {
		//logger.info("SQL:{},params:{}", sql, params);
		if (this.batch != null) {
			PreparedStatement statement = this.prepareStatement(sql);
			this.batch.add(statement.bind(params));
			return null;
		}
		PreparedStatement statement = this.prepareStatement(sql);
		this.session.executeAsync(statement.bind(params));
		return null;
	}

	private PreparedStatement prepareStatement(String sql) {
		PreparedStatement statement = this.statements.get(sql);
		if (statement != null) {
			return statement;
		}
		statement = this.session.prepare(sql);
		this.statements.put(sql, statement);
		return statement;
	}

	@Override
	public Map<String, Object> commit() {
		if (this.batch != null) {
			this.session.executeAsync(this.batch);
		}
		this.batch = null;
		return null;
	}

	@Override
	public void close() {
		this.batch = null;
		this.session.close();
	}

}
