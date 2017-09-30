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

	public List<Map<String, Object>> queryBaseEntities(String kid);

	public Map<String, Object> queryEntity(String eid);

	public List<Map<String, Object>> queryEntityModifies(String eid);

	public List<Map<String, Object>> queryBaseLabels(String kid);

	public List<Map<String, Object>> queryLabelModifies(String lid);

	public Map<String, Object> queryMaterial(String mid);

	public List<Map<String, Object>> queryMaterials(String kid);

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
	 * add new knowledge base
	 * @param kid
	 * @param name
	 * @param desc
	 */
	public void addBase(String kid, String name, String desc);

	/**
	 * add new label
	 * @param kid
	 * @param lid
	 * @param name
	 * @param properties
	 */
	public void addLabel(String kid, String lid, String name, Map<String, Object> properties);

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

	public Map<String, Object> createLabel(String kid, String labelName, List<String> parents, List<String> links,
			List<String> properties);

	public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
			List<String> links, List<String> properties);

	public Map<String, Object> updateEntity(String kid, String eid, List<Map<String, Object>> modifies);

	public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties);

	public Map<String, Object> addEntity(String kid, String name, List<String> labels, Map<String, Object> properties);

	public void addLink(String name, String sourceid, String sourcename, String targetid, String targetname,
			Map<String, Object> properties);

	/**
	 * begin batch
	 */
	public void beginBatch();

	/**
	 * commit batch
	 */
	public Map<String, Object> commit();

	public void close();

}
