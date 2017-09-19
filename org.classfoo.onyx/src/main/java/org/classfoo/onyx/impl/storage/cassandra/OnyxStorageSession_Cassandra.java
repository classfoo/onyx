package org.classfoo.onyx.impl.storage.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * @see OnyxStorageSession
 * @author ClassFoo
 *
 */
public class OnyxStorageSession_Cassandra implements OnyxStorageSession {

	private Session session;

	public OnyxStorageSession_Cassandra(Session session) {
		this.session = session;
	}

	@Override
	public List<Map<String, Object>> queryBases() {
		ResultSet value = session.execute("select * from bases");
		return convertToList(value);
	}

	@Override
	public Map<String, Object> queryBase(String id) {
		ResultSet value = session.execute("select * from bases where id_=?", id);
		return convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryBaseEntities(String kid) {
		ResultSet value = session.execute("select * from base_entity where kid_=? LIMIT 500", kid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryEntityModifies(String eid) {
		ResultSet value = session.execute("select * from entities where id_=?", eid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryBaseLabels(String kid) {
		ResultSet value = session.execute("select * from base_label where kid_=?", kid);
		return convertToList(value);
	}

	@Override
	public List<Map<String, Object>> queryLabelModifies(String lid) {
		ResultSet value = session.execute("select * from labels where id_=? ALLOW FILTERING", lid);
		return this.convertToList(value);
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
		ResultSet value = session.execute(
				"insert into labels (id,kid,name,parents,links,properties) values (?,?,?,?,?,?)", lid, kid, labelName,
				parents, links, properties);
		return convertToMap(value);
	}

	@Override
	public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
			List<String> links, List<String> properties) {
		ResultSet value = session.execute(
				"update labels set name=?,parents=?,links=?,properties=? where kid=? and id=?", labelName, parents,
				links, properties, kid, lid);
		return convertToMap(value);
	}

	@Override
	public Map<String, Object> addEntity(String kid, String name, Map<String, Object> properties) {
		String eid = OnyxUtils.getRandomUUID("e");
		ResultSet value = session.execute(
				"insert into entities (kid_,id_,name_,event_,order_,property_,operate_,key_,value_,user_) values(?,?,?,now(),1,'name','add',?,?,?)",
				kid, eid, name, name, null, "admin");
		session.execute("insert into base_entity (kid_,id_,name_) values(?,?,?)", kid, eid, name);
		return convertToMap(value);
	}

	@Override
	public Map<String, Object> updateEntity(String kid, String name, List<Map<String, Object>> modifies) {
		String eid = OnyxUtils.getRandomUUID("e");
		ResultSet value = session.execute(
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
			session.execute(
					"insert into labels (id_,event_,order_,key_,operate_,name_,parent_,ptype_,pname_,poptions_,kid_,user_) values (?,now(),?,?,?,?,?,?,?,?,?,?)",
					lid, i, key, operate, name, parent, ptype, pname, poptions, kid, "admin");
		}
		return null;
	}

	@Override
	public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties) {
		String mid = OnyxUtils.getRandomUUID("m");
		ResultSet value = session.execute("insert into materials (id_,kid_,name_,desc_,properties_) values (?,?,?,?,?)",
				mid, kid, name, desc, properties);
		HashMap<String, Object> result = new HashMap<String, Object>(4);
		result.put("id", mid);
		result.put("name", name);
		result.put("desc", desc);
		result.put("kid", kid);
		return result;
	}

	@Override
	public Map<String, Object> queryMaterial(String mid) {
		ResultSet value = session.execute("select * from materials where id_=?", mid);
		return convertToMap(value);
	}

	@Override
	public List<Map<String, Object>> queryMaterials(String kid) {
		ResultSet value = session.execute("select * from materials");
		return convertToList(value);
	}

	@Override
	public Map<String, Object> commit() {
		return null;
	}

	@Override
	public void close() {
		this.session.close();
	}
}
