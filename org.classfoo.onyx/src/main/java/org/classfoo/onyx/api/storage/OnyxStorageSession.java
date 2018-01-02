package org.classfoo.onyx.api.storage;

import java.util.List;
import java.util.Map;

/**
 * Onyx Storage Session
 * @author ClassFoo
 *
 */
public interface OnyxStorageSession {

    /**
     * Query knowledge base by id
     * @param id
     * @return
     */
    public Map<String, Object> queryBase(String id);

    /**
     * Query knowledge bases
     * @return
     */
    public List<Map<String, Object>> queryBases();

    /**
     * Query Entities
     * @return
     */
    public List<Map<String, Object>> queryEntities();

    /**
     * Query Entities By Knowledge Base id
     * @param kid
     * @return
     */
    public List<Map<String, Object>> queryBaseEntities(String kid);

    /**
     * Query Entity by entity id
     * @param eid
     * @return
     */
    public Map<String, Object> queryEntity(String eid);

    /**
     * Query Entity modifies by entity id
     * @param eid
     * @return
     */
    public List<Map<String, Object>> queryEntityModifies(String eid);

    /**
     * query entity events
     * @param eid
     * @return
     */
    public List<Map<String, Object>> queryEntityEvents(String eid);

    /**
     * query label by knowledge base id and label name
     * @param kid
     * @param name
     * @return
     */
    public Map<String, Object> queryBaseLabel(String kid, String name);

    /**
     * query labels by knowledge base id
     * @param kid
     * @return
     */
    public List<Map<String, Object>> queryBaseLabels(String kid);

    /**
     * query label modifies by label id
     * @param lid
     * @return
     */
    public List<Map<String, Object>> queryLabelModifies(String lid);

    /**
     * query material by material id
     * @param mid
     * @return
     */
    public Map<String, Object> queryMaterial(String mid);

    /**
     * query materials by knowledge base id
     * @param kid
     * @return
     */
    public List<Map<String, Object>> queryMaterials(String kid);

    /**
     * query all links of entity
     * @param eid
     * @return
     */
    public List<Map<String, Object>> queryLinks(String eid);

    /**
     * query all link names of entity
     * @param eid
     * @return
     */
    public List<Map<String, Object>> queryLinkNames(String eid);

    /**
     * query linked nodes by entity
     * @param eid
     * @param options
     * @return
     */
    public Map<String, Object> queryLinkNodes(String eid, Map<String, Object> options);

    /**
     * query graphs
     * @return
     */
    public List<Map<String, Object>> queryGraphs();

    /**
     * query graphs in base
     * @param kid
     * @return
     */
    public List<Map<String, Object>> queryBaseGraphs(String kid);

    /**
     * add new knowledge base
     * @param kid
     * @param name
     * @param desc
     * @return 
     */
    public Map<String, Object> addBase(String kid, String name, String desc);

    /**
     * add new label
     * @param kid
     * @param lid
     * @param name
     * @param properties
     * @return
     */
    public Map<String, Object> addLabel(String kid, String lid, String name, Map<String, Object> properties);

    /**
     * save label modifies
     * @param kid
     * @param lid
     * @param labelName
     * @param modifies
     * @return
     */
    public Map<String, Object> saveLabelModifies(String kid, String lid, String labelName,
            List<Map<String, Object>> modifies);

    /**
     * Update knowledge base name
     * @param id
     * @param newname
     */
    public void updateBaseName(String id, String newname);

    /**
     * Update knowledge base caption
     * @param id
     * @param newcaption
     */
    public void updateBaseCaption(String id, String newcaption);

    /**
     * Update knowledge base properties
     * @param id
     * @param newproperties
     */
    public void updateBaseProperties(String id, Map<String, Object> newproperties);

    /**
     * create label
     * @param kid
     * @param labelName
     * @param parents
     * @param links
     * @param properties
     * @return
     */
    public Map<String, Object> createLabel(String kid, String labelName, List<String> parents, List<String> links,
            List<String> properties);

    /**
     * update label
     * @param kid
     * @param lid
     * @param labelName
     * @param parents
     * @param links
     * @param properties
     * @return
     */
    public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
            List<String> links, List<String> properties);

    /**
     * update entity
     * @param kid
     * @param eid
     * @param modifies
     * @return
     */
    public Map<String, Object> updateEntity(String kid, String eid, List<Map<String, Object>> modifies);

    /**
     * add material
     * @param name
     * @param desc
     * @param kid
     * @param properties
     * @return
     */
    public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties);

    /**
     * add entity
     * @param kid
     * @param name
     * @param labels
     * @param properties
     * @return
     */
    public Map<String, Object> addEntity(String kid, String name, List<String> labels, Map<String, Object> properties);

    /**
     * update entity
     * @param kid
     * @param eid
     * @param labels
     * @param properties
     * @return
     */
    public Map<String, Object> updateEntity(String kid, String eid, List<String> labels,
            Map<String, Object> properties);

    /**
     * add labels for entity
     * @param eid
     * @param labels
     * @return
     */
    public Map<String, Object> addEntityLabels(String eid, List<String> labels);

    /**
     * add properties for entity
     * @param id
     * @param properties
     * @return
     */
    public Map<String, Object> addEntityProperties(String id, Map<String, Object> properties);

    /**
     * add event of entity
     * @param eid
     * @param type
     * @param name
     * @param date
     * @param properties
     */
    public void addEvent(String eid, String type, String name, String date, Map<String, Object> properties);

    /**
     * add link
     * @param name
     * @param sourceid
     * @param sourcename
     * @param targetid
     * @param targetname
     * @param properties
     */
    public void addLink(String name, String sourceid, String sourcename, String targetid, String targetname,
            Map<String, Object> properties);

    /**
     * add graph
     * @param name
     * @param kid
     * @param content
     * @param properties
     */
    public void addGraph(String name, String kid, String content, Map<String, Object> properties);

    /**
     * begin batch
     */
    public void beginBatch();

    /**
     * commit batch
     * @return
     */
    public Map<String, Object> commit();

    /**
     * close session
     */
    public void close();

}
