package org.classfoo.onyx.impl.storage.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.impl.OnyxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Onyx Storage In Cassandra
 * @see OnyxStorage
 * @author ClassFoo
 *
 */
public class OnyxStorage_Cassandra implements OnyxStorage {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStorage_Cassandra.class);

	private volatile Cluster cluster;

	public OnyxStorage_Cassandra() {

	}

	private Cluster getCluster() {
		if (this.cluster != null) {
			return this.cluster;
		}
		synchronized (this) {
			if (this.cluster != null) {
				return this.cluster;
			}
			this.cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
			this.init(this.cluster);
			return this.cluster;
		}
	}

	private void init(Cluster cluster) {
		try (Session session = cluster.connect("onyx")) {
			logger.info("start initialize cassandra tables...");
			try {
				session.execute("drop table bases");
				session.execute("drop table entities");
				session.execute("drop table base_entity");
				session.execute("drop table labels");
				session.execute("drop table base_label");
				session.execute("drop table materials");
				session.execute("drop table files");
				session.execute("drop table timeline");
			}
			catch (Exception e) {

			}
			session.execute(
					"create table if not exists bases(kid_ text,name_ text, desc_ text,primary key(kid_,name_) )");
			session.execute(
					"create table if not exists entities(kid_ text, eid_ text, name_ text,property_ text,operate_ text,key_ text,value_ map<text,text>,event_ timeuuid,order_ int,user_ text, primary key (eid_,event_,order_,property_,operate_,key_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
			session.execute(
					"create table if not exists base_entity(kid_ text, eid_ text, name_ text,primary key (kid_,eid_,name_)) WITH CLUSTERING ORDER BY (eid_ asc,name_ asc)");
			session.execute(
					"create table if not exists labels(lid_ text, event_ timeuuid,order_ int,key_ text, operate_ text,name_ text,parent_ text,pname_ text,ptype_ text,poptions_ map<text,text>,kid_ text, user_ text, primary key (lid_,event_,order_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
			session.execute(
					"create table if not exists base_label(kid_ text, lid_ text, name_ text,primary key (kid_,lid_,name_)) WITH CLUSTERING ORDER BY (lid_ asc,name_ asc)");
			session.execute(
					"create table if not exists materials(id_ text, kid_ text,user_ text,name_ text, desc_ text, properties_ map<text,text>,primary key (id_))");
			session.execute(
					"create table if not exists files(id_ text, kid_ text,mid_ text,user_ text,name_ text, desc_ text, primary key (id_))");
			session.execute(
					"create table if not exists material_file(mid_ text,fid_ text,fname_ text, primary key (mid_,fid_))");
			session.execute(
					"create table if not exists timeline(id_ text,time_ text,kid_ text,user_ text, type_ text, relate_ text, content_ text,properties_ map<text,text>, primary key (id_,time_,kid_)) WITH CLUSTERING ORDER BY (time_ desc,kid_ asc)");
			this.initTestDatas(session);
			logger.info("finish initialize cassandra tables！");
		}
		catch (Exception e) {
			logger.error("Error while initialize cassandra tables!", e);
		}
	}

	private void initTestDatas(Session session) {
		String kid = OnyxUtils.getRandomUUID("k");
		session.execute("insert into bases (kid_,name_,desc_) values(?,?,?)", kid, "娱乐圈知识库",
				"娱乐圈知识库，明星档案，绯闻事件，演艺公司，娱乐圈周边");
		for (int i = 1; i <= 100; i++) {
			String eid = OnyxUtils.getRandomUUID("e");
			String entityName = "实体" + i;
			session.execute(
					"insert into entities (kid_,eid_,name_,event_,order_,property_,operate_,key_,value_,user_) values(?,?,?,now(),1,'name','add',?,?,?)",
					kid, eid, entityName, entityName, null, "admin");
			session.execute("insert into base_entity (kid_,eid_,name_) values(?,?,?)", kid, eid, entityName);
			String lid = OnyxUtils.getRandomUUID("l");
			String labelName = "标签" + i;
			session.execute(
					"insert into labels (kid_,lid_,event_,order_,key_,name_,operate_,pname_,ptype_,poptions_,user_) values(?,?,now(),1,?,?,'add',null,null,null,'admin')",
					kid, lid, "name", labelName);
			session.execute("insert into base_label (kid_,lid_,name_) values(?,?,?)", kid, lid, labelName);
		}
	}

	@Override
	public List<Map<String, Object>> queryBases() {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from bases");
			return convertToList(value);
		}
	}

	@Override
	public Map<String, Object> queryBase(String id) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from bases where kid_=?", id);
			return convertToMap(value);
		}
		catch (Exception e) {
			logger.error("Error while query knowledge base!", e);
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryBaseEntities(String kid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from base_entity where kid_=?", kid);
			return convertToList(value);
		}
	}

	@Override
	public List<Map<String, Object>> queryEntityModifies(String eid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from entities where eid_=?", eid);
			return convertToList(value);
		}
		catch (Exception e) {
			logger.error("Error while query knowledge base!", e);
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryBaseLabels(String kid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from base_label where kid_=?", kid);
			return convertToList(value);
		}
	}

	@Override
	public List<Map<String, Object>> queryLabelModifies(String lid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from labels where lid_=? ALLOW FILTERING", lid);
			return this.convertToList(value);
		}
		catch (Exception e) {
			logger.error("Error while query knowledge base label!", e);
			return null;
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
		try (Session session = this.getCluster().connect("onyx")) {
			String lid = OnyxUtils.getRandomUUID("l");
			ResultSet value = session.execute(
					"insert into labels (lid,kid,name,parents,links,properties) values (?,?,?,?,?,?)", lid, kid,
					labelName, parents, links, properties);
			return convertToMap(value);
		}
		catch (Exception e) {
			logger.error("Error while create knowledge base label!", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
			List<String> links, List<String> properties) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute(
					"update labels set name=?,parents=?,links=?,properties=? where kid=? and lid=?", labelName, parents,
					links, properties, kid, lid);
			return convertToMap(value);
		}
		catch (Exception e) {
			logger.error("Error while update knowledge base label!", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> updateEntity(String kid, String eid, List<Map<String, Object>> modifies) {
		return null;
	}

	@Override
	public Map<String, Object> saveLabelModifies(String kid, String lid, String labelName,
			List<Map<String, Object>> modifies) {
		try (Session session = this.getCluster().connect("onyx")) {
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
						"insert into labels (lid_,event_,order_,key_,operate_,name_,parent_,ptype_,pname_,poptions_,kid_,user_) values (?,now(),?,?,?,?,?,?,?,?,?,?)",
						lid, i, key, operate, name, parent, ptype, pname, poptions, kid, "admin");
			}
			return null;
		}
		catch (Exception e) {
			logger.error("Error while update knowledge base label!", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties) {
		try (Session session = this.getCluster().connect("onyx")) {
			String mid = OnyxUtils.getRandomUUID("m");
			ResultSet value = session.execute(
					"insert into materials (id_,kid_,name_,desc_,properties_) values (?,?,?,?,?)", mid, kid, name, desc,
					properties);
			HashMap<String, Object> result = new HashMap<String, Object>(1);
			result.put("id", mid);
			result.put("name", name);
			result.put("desc", desc);
			result.put("kid", kid);
			return result;
		}
		catch (Exception e) {
			logger.error("Error while create knowledge base label!", e);
			return null;
		}
	}

	@Override
	public Map<String, Object> queryMaterial(String mid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from materials where id_=?", mid);
			return convertToMap(value);
		}
		catch (Exception e) {
			logger.error("Error while query materials!", e);
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryMaterials(String kid) {
		try (Session session = this.getCluster().connect("onyx")) {
			ResultSet value = session.execute("select * from materials");
			return convertToList(value);
		}
	}
}
