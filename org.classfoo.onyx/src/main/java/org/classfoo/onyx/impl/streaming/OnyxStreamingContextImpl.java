package org.classfoo.onyx.impl.streaming;

import java.util.HashMap;
import java.util.Map;

import org.classfoo.onyx.api.streaming.OnyxStreamingContext;

/**
 * @see OnyxStreamingContext
 * @author ClassFoo
 *
 */
public class OnyxStreamingContextImpl implements OnyxStreamingContext {

	private Map<String, Map<String, Map<String, Object>>> entitiesCache = new HashMap<String, Map<String, Map<String, Object>>>();

	@Override
	public void putEntityByProperty(String property, String value, Map<String, Object> entity) {
		Map<String, Map<String, Object>> map = entitiesCache.get(property);
		if (map == null) {
			map = new HashMap<String, Map<String, Object>>();
			entitiesCache.put(property, map);
		}
		map.put(value, entity);
	}

	@Override
	public Map<String, Object> getEntityByProperty(String property, String value) {
		Map<String, Map<String, Object>> map = this.entitiesCache.get(property);
		if (map == null) {
			return null;
		}
		return map.get(value);
	}

	@Override
	public void putLabel(String id, String label) {
		// TODO Auto-generated method stub

	}

}
