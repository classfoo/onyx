package org.classfoo.onyx.impl.storage.cassandra;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.storage.AbstractOnyxStorage;
import org.classfoo.onyx.impl.storage.datas.neeq.NeeqData;
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
public class OnyxStorageCassandra extends AbstractOnyxStorage implements OnyxStorage {

    private static final int MOCK_SIZE = 100;

    private static final Logger logger = LoggerFactory.getLogger(OnyxStorageCassandra.class);

    private OnyxService onyxService;

    private volatile Cluster cluster;

    public OnyxStorageCassandra(OnyxService onyxService) {
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
            /*this.init(this.cluster);*/
            return this.cluster;
        }
    }

    private void init(Cluster cluster) {
        this.createKeySpace(cluster);
        this.clearIndexes();
        Session session = cluster.connect("onyx");
        try {
            logger.info("start initialize cassandra tables...");
            this.clearTables(session);
            this.createTables(session);
            /*this.initBaseData_YLQ(session);*/
            this.initBaseDataNEEQ();
            logger.info("finish initialize cassandra tables！");
        }
        catch (Exception e) {
            logger.error("Error while initialize cassandra tables!", e);
        }
        finally {
            session.close();
        }
    }

    private void createKeySpace(Cluster cluster) {
        Session session = cluster.connect();
        try {
            session.execute(
                    "create keyspace IF NOT EXISTS onyx WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3}");
        }
        finally {
            session.close();
        }
    }

    private void clearIndexes() {
        OnyxIndexSession indexSession = this.onyxService.getIndexService().openSession();
        try {
            indexSession.clearIndexes();
        }
        finally {
            indexSession.close();
        }
    }

    private void clearTables(Session session) {
        try {
            long start = System.currentTimeMillis();
            logger.info("开始清理数据库表...");
            session.execute("drop table bases");
            session.execute("drop table base_entity");
            session.execute("drop table base_label");
            session.execute("drop table entities");
            session.execute("drop table entity_property");
            session.execute("drop table property_entity");
            session.execute("drop table entity_label");
            session.execute("drop table label_entity");
            session.execute("drop table labels");
            session.execute("drop table materials");
            session.execute("drop table files");
            session.execute("drop table timeline");
            session.execute("drop table links");
            session.execute("drop table links_source");
            session.execute("drop table links_target");
            session.execute("drop table events");
            session.execute("drop table entity_event");
            session.execute("drop table graphes");
            long end = System.currentTimeMillis();
            logger.info("清理数据库表完毕，耗时：{}秒！", ((double) (end - start)) / 1000);
        }
        catch (Exception e) {

        }
    }

    private void createTables(Session session) {
        logger.info("开始创建数据库表...");
        long start = System.currentTimeMillis();
        session.execute(
                "CREATE KEYSPACE if not exists onyx WITH REPLICATION={'class':'SimpleStrategy','replication_factor':3};");
        session.execute("create table if not exists bases(id_ text,name_ text, desc_ text,primary key(id_,name_) )");
        //			session.execute(
        //					"create table if not exists entities(id_ text, kid_ text, name_ text,property_ text,operate_ text,key_ text,value_ map<text,text>,event_ timeuuid,order_ int,user_ text, primary key (id_,event_,order_,property_,operate_,key_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
        session.execute(
                "create table if not exists entities(id_ text,kid_ text,name_ text, labels_ list<text>, properties_ map<text,text>,primary key (id_))");
        session.execute(
                "create table if not exists entity_property(id_ text, key_ text, value_ text,primary key (id_,key_))");
        session.execute(
                "create table if not exists property_entity(key_ text, value_ text,id_ text, primary key (key_,value_,id_))");
        session.execute(
                "create table if not exists entity_label(id_ text, label_ text, name_ text,primary key (id_,label_))");
        session.execute(
                "create table if not exists label_entity(label_ text, id_ text, name_ text, primary key (label_,id_))");
        session.execute(
                "create table if not exists base_entity(kid_ text, id_ text, name_ text,primary key (kid_,id_,name_)) WITH CLUSTERING ORDER BY (id_ asc,name_ asc)");
        //			session.execute(
        //					"create table if not exists labels(id_ text, event_ timeuuid,order_ int,key_ text, operate_ text,name_ text,parent_ text,pname_ text,ptype_ text,poptions_ map<text,text>,kid_ text, user_ text, primary key (id_,event_,order_)) WITH CLUSTERING ORDER BY (event_ asc,order_ asc)");
        session.execute(
                "create table if not exists labels(name_ text, kid_ text,id_ text,properties_ map<text, text>, primary key (name_,kid_))");
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
        session.execute(
                "create table if not exists links(id_ text, source_ text,target_ text,name_ text,properties_ map<text,text>, primary key (id_,source_,target_,name_))");
        session.execute(
                "create table if not exists links_source(source_ text,sourcename_ text, name_ text,target_ text,targetname_ text, id_ text, properties_ map<text,text>, primary key (source_,name_,target_))");
        session.execute(
                "create table if not exists links_target(target_ text,targetname_ text,name_ text,source_ text,sourcename_ text, id_ text, properties_ map<text,text>, primary key (target_,name_,source_))");
        session.execute(
                "create table if not exists events(id_ text, eid_ text,time_ text,name_ text,type_ text, properties_ map<text,text>, primary key (id_))");
        session.execute(
                "create table if not exists entity_event(id_ text, time_ text,name_ text,type_ text,eid_ text, primary key (id_,time_,name_,type_,eid_)) WITH CLUSTERING ORDER BY (time_ desc)");
        session.execute(
                "create table if not exists graphs(id_ text,kid_ text, name_ text,content_ text,properties_ map<text,text>, primary key (id_))");
        session.execute(
                "create table if not exists base_graph(kid_ text, id_ text, name_ text, primary key (kid_,id_,name_))");

        long end = System.currentTimeMillis();
        logger.info("创建数据库表完毕，耗时：{}秒！", ((double) (end - start)) / 1000);
    }

    private void initBaseDataYLQ(Session session) {
        String kid = OnyxUtils.getRandomUUID("k");
        session.execute("insert into bases (id_,name_,desc_) values(?,?,?)", kid, "娱乐圈知识库",
                "娱乐圈知识库，明星档案，绯闻事件，演艺公司，娱乐圈周边");
        for (int i = 1; i <= MOCK_SIZE; i++) {
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

    private void initBaseDataNEEQ() {
        OnyxStorageSession session = this.openSession();
        try {
            logger.info("开始初始化新三板数据...");
            NeeqData data = new NeeqData(this.onyxService);
            data.initTestData(session);
        }
        finally {
            session.close();
        }
    }

    @Override
    public OnyxStorageSession openSession() {
        Session session = this.getCluster().connect("onyx");
        return new OnyxStorageSessionCassandra(this.onyxService, this, session);
    }
}
