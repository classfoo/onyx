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

	public List<Map<String, Object>> queryEntityModifies(String eid);

	public List<Map<String, Object>> queryBaseLabels(String kid);

	public List<Map<String, Object>> queryLabelModifies(String lid);

	/**
	 * Create new knowledge base
	 * @param base
	 */
	public void createKnowledgeBase(Map<String, Object> base);

	public Map<String, Object> saveLabelModifies(String kid, String lid, String labelName,
			List<Map<String, Object>> modifies);

	/**
	 * Update knowledge base name
	 * @param id
	 * @param newname
	 */
	public void updateKnowledgeBaseName(String id, String newname);

	/**
	 * Update knowledge base caption
	 * @param id
	 * @param newcaption
	 */
	public void updateKnowledgeBaseCaption(String id, String newcaption);

	/**
	 * Update knowledge base properties
	 * @param id
	 * @param newproperties
	 */
	public void updateKnowledgeBaseProperties(String id, Map<String, Object> newproperties);

	public Map<String, Object> createLabel(String kid, String labelName, List<String> parents, List<String> links,
			List<String> properties);

	public Map<String, Object> updateLabel(String kid, String lid, String labelName, List<String> parents,
			List<String> links, List<String> properties);

	public Map<String, Object> updateEntity(String kid, String eid, List<Map<String, Object>> modifies);

	public Map<String, Object> addMaterial(String name, String desc, String kid, Map<String, Object> properties);

	public Map<String, Object> queryMaterial(String mid);

	public List<Map<String, Object>> queryMaterials(String kid);

	public Map<String, Object> addEntity(String kid, String name, Map<String, Object> properties);

	public void addLink(String name, String sourceid, String targetid, Map<String, Object> properties);

	public Map<String, Object> commit();

	public void close();

}