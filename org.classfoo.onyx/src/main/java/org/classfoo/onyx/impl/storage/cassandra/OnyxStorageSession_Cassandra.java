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
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

/**
 * @see OnyxStorageSession
 * @author ClassFoo
 *
 */
public class OnyxStorageSession_Cassandra implements OnyxStorageSession {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStorageSession_Cassandra.class);

	private Session session;

	private OnyxStorage storage;

	private Map<String, PreparedStatement> statements = new ConcurrentHashMap<String, PreparedStatement>();

	private BatchStatement batch;

	public OnyxStorageSession_Cassandra(OnyxStorage storage, Session session) {
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
	public List<Map<String, Object>> queryBaseEntities(String kid) {
		ResultSet value = this.executeQuery("select * from base_entity where kid_=? LIMIT 32", kid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryEntityModifies(String eid) {
		ResultSet value = this.executeQuery("select * from entities where id_=?", eid);
		return convertToList(value);
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
		List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>(10);
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>(10);
		if ("in".equals(type)) {
			ResultSet sourceValue = this.executeQuery(
					"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_target where target_=? and name_=?",
					eid, name);
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
		else {
			ResultSet sourceValue = this.executeQuery(
					"select id_,name_,source_,sourcename_,target_,targetname_,properties_ from links_source where source_=? and name_=?",
					eid, name);
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
		HashMap<String, Object> result = new HashMap<String, Object>(2);
		result.put("entities", entities);
		result.put("links", links);
		return result;
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
	public void createKnowledgeBase(Map<String, Object> base) {

	}

	@Override
	public void updateKnowledgeBaseName(String id, String newname) {

	}

	@Override
	public void updateKnowledgeBaseCaption(String id, String newcaption) {

	}

	@Override
	public void updateKnowledgeBaseProperties(String id, Map<String, Object> newproperties) {

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
	public Map<String, Object> addEntity(String kid, String name, Map<String, Object> properties) {
		String eid = OnyxUtils.getRandomUUID("e");
		this.executeUpdate("insert into entities (kid_,id_,name_,labels_, properties_) values(?,?,?,?,?)", kid, eid,
				name, null, properties);
		this.executeUpdate("insert into base_entity (kid_,id_,name_) values(?,?,?)", kid, eid, name);
		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("id", eid);
		entity.put("kid", kid);
		entity.put("name", name);
		entity.put("properties", properties);
		return entity;
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
