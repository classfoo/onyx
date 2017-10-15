package org.classfoo.onyx.api.index;

import java.util.List;
import java.util.Map;

/**
 * Onyx Index Service
 * @author ClassFoo
 *
 */
public interface OnyxIndexService {
	//
	//	/**
	//	 * open onyx index session
	//	 * @return
	//	 */
	//	public OnyxIndexSession openSession();

	public void addEntityIndex(Map<String, Object> json);
	
	public List<Map<String, Object>> searchEntity(String name);

	public void clearIndexes();
}
