package org.classfoo.onyx.api.streaming;

import java.util.Map;

/**
 * Onyx Streaming Context
 * @author ClassFoo
 *
 */
public interface OnyxStreamingContext {

	/**
	 * cache a entity by property and value
	 * @param property
	 * @param value
	 * @param entity
	 */
	public void putEntityByProperty(String property, String value, Map<String, Object> entity);

	/**
	 * get a cached entity by property and value
	 * @param property
	 * @param value
	 * @return
	 */
	public Map<String, Object> getEntityByProperty(String property, String value);

}
