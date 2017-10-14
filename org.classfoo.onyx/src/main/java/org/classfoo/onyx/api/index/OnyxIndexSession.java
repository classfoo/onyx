package org.classfoo.onyx.api.index;

import java.util.Map;

/**
 * Onyx Index Session
 * @author ClassFoo
 *
 */
public interface OnyxIndexSession {

	/**
	 * add entity index
	 * @param entity
	 */
	public void addEntityIndex(Map<String, Object> entity);

	public void addLabelIndex(Map<String, Object> label);
}
