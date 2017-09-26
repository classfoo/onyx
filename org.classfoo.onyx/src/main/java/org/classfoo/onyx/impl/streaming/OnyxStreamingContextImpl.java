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

	private Map<String, Map<String, String>> entityids = new HashMap<String, Map<String, String>>();

	@Override
	public String putEntityIdByProperty(String property, String value, String id) {
		Map<String, String> map = entityids.get(property);
		if (map == null) {
			map = new HashMap<String, String>();
			entityids.put(property, map);
		}
		map.put(value, id);
		return null;
	}

	@Override
	public String getEntityIdByProperty(String property, String value) {
		Map<String, String> map = this.entityids.get(property);
		if (map == null) {
			return null;
		}
		return map.get(value);
	}

}
