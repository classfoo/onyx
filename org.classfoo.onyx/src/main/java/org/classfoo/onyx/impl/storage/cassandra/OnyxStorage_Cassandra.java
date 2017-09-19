package org.classfoo.onyx.impl.storage.cassandra;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.storage.datas.neeq.NEEQData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Onyx Storage In Cassandra
 * @see OnyxStorage
 * @author ClassFoo
 *
 */
public class OnyxStorage_Cassandra implements OnyxStorage {

	private static final Logger logger = LoggerFactory.getLogger(OnyxStorage_Cassandra.class);

	private OnyxService onyxService;

	private volatile Cluster cluster;

	public OnyxStorage_Cassandra(OnyxService onyxService) {
		this.onyxService = onyxService;
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
		Session session = cluster.connect("onyx");
		try {
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
					"CREATE KEYSPACE if not exists onyx WITH REPLICATION={'class':'SimpleStrategy','replication_factor':3};");
			session.execute(
					"create table if not exists bases(id_ text,name_ text, desc_ text,primary key(id_,name_) )");
			session.execute(
					"create table if not exists entities(id_ text, kid_ text, name_ text,property_ text,operate_ text,key_ text,value_ map<text,text>,event_ timeuuid,order_ int,user_ text, primary key (id_,event_,order_,property_,operate_,key_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
			session.execute(
					"create table if not exists base_entity(kid_ text, id_ text, name_ text,primary key (kid_,id_,name_)) WITH CLUSTERING ORDER BY (id_ asc,name_ asc)");
			session.execute(
					"create table if not exists labels(id_ text, event_ timeuuid,order_ int,key_ text, operate_ text,name_ text,parent_ text,pname_ text,ptype_ text,poptions_ map<text,text>,kid_ text, user_ text, primary key (id_,event_,order_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
			session.execute(
					"create table if not exists base_label(kid_ text, id_ text, name_ text,primary key (kid_,id_,name_)) WITH CLUSTERING ORDER BY (id_ asc,name_ asc)");
			session.execute(
					"create table if not exists materials(id_ text, kid_ text,user_ text,name_ text, desc_ text, properties_ map<text,text>,primary key (id_))");
			session.execute(
					"create table if not exists files(id_ text, kid_ text,mid_ text,user_ text,name_ text, desc_ text, primary key (id_))");
			session.execute(
					"create table if not exists material_file(mid_ text,id_ text,fname_ text, primary key (mid_,id_))");
			session.execute(
					"create table if not exists timeline(id_ text,time_ text,kid_ text,user_ text, type_ text, relate_ text, content_ text,properties_ map<text,text>, primary key (id_,time_,kid_)) WITH CLUSTERING ORDER BY (time_ desc,kid_ asc)");
			//this.initBaseData_YLQ(session);
			this.initBaseData_NEEQ(session);
			logger.info("finish initialize cassandra tables！");
		}
		catch (Exception e) {
			logger.error("Error while initialize cassandra tables!", e);
		}
		finally {
			session.close();
		}
	}

	private void initBaseData_YLQ(Session session) {
		String kid = OnyxUtils.getRandomUUID("k");
		session.execute("insert into bases (id_,name_,desc_) values(?,?,?)", kid, "娱乐圈知识库",
				"娱乐圈知识库，明星档案，绯闻事件，演艺公司，娱乐圈周边");
		for (int i = 1; i <= 100; i++) {
			String eid = OnyxUtils.getRandomUUID("e");
			String entityName = "实体" + i;
			session.execute(
					"insert into entities (kid_,id_,name_,event_,order_,property_,operate_,key_,value_,user_) values(?,?,?,now(),1,'name','add',?,?,?)",
					kid, eid, entityName, entityName, null, "admin");
			session.execute("insert into base_entity (kid_,id_,name_) values(?,?,?)", kid, eid, entityName);
			String lid = OnyxUtils.getRandomUUID("l");
			String labelName = "标签" + i;
			session.execute(
					"insert into labels (kid_,id_,event_,order_,key_,name_,operate_,pname_,ptype_,poptions_,user_) values(?,?,now(),1,?,?,'add',null,null,null,'admin')",
					kid, lid, "name", labelName);
			session.execute("insert into base_label (kid_,id_,name_) values(?,?,?)", kid, lid, labelName);
		}
	}

	private void initBaseData_NEEQ(Session session) {
		String kid = OnyxUtils.getRandomUUID("k");
		session.execute("insert into bases (id_,name_,desc_) values(?,?,?)", kid, "新三板知识库",
				"新三板知识库，新三板公司档案，投资人信息，股权关系，交易意向");
		NEEQData data = new NEEQData(this.onyxService);
		data.initTestData(kid);
	}

	@Override
	public OnyxStorageSession openSession() {
		Session session = this.getCluster().connect("onyx");
		return new OnyxStorageSession_Cassandra(session);
	}

}
