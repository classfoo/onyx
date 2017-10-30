package org.classfoo.onyx.api.index;

import java.util.List;
import java.util.Map;

/**
 * Onyx Index Session
 * @author ClassFoo
 *
 */
public interface OnyxIndexSession {

	/**
	 * add index
	 * @param index
	 * @param type
	 * @param id
	 * @param object
	 */
	public void addIndex(String index, String type, String id, Map<String, Object> object);

	/**
	 * merge index
	 * @param index
	 * @param type
	 * @param id
	 * @param object
	 */
	public void mergeIndex(String index, String type, String id, Map<String, Object> object);

	/**
	 * update index
	 * @param index
	 * @param type
	 * @param id
	 * @param object
	 */
	public void updateIndex(String index, String type, String id, Map<String, Object> object);

	/**
	 * update or insert index
	 * @param index
	 * @param type
	 * @param id
	 * @param object
	 */
	public void upsertIndex(String index, String type, String id, Map<String, Object> object);

	/**
	 * add entity type item
	 * @param json
	 */
	public void addEntityIndex(Map<String, Object> json);

	/**
	 * search entity type item
	 * @param name
	 * @return
	 */
	public List<Map<String, Object>> searchEntities(String name);

	/**
	 * search entities by base
	 * @param kid
	 * @return
	 */
	public List<Map<String, Object>> searchBaseEntities(String kid, String name);

	/**
	 * add label index
	 * @param json
	 */
	public void addLabelIndex(Map<String, Object> json);

	/**
	 * search labels by name
	 * @param text
	 * @return
	 */
	public List<Map<String, Object>> searchLabels(String name);

	//	/**
	//	 * Add entity with same name into one index item
	//	 * @param name
	//	 * @param json
	//	 */
	//	public void addNameIndex(String name, Map<String, Object> json);
	//
	//	/**
	//	 * search entity with same name
	//	 * @param name
	//	 * @return
	//	 */
	//	public List<Map<String, Object>> searchNameIndex(String name);

	/**
	 * clear all indexes
	 */
	public void clearIndexes();

	/**
	 * close session
	 */
	public void close();

}
