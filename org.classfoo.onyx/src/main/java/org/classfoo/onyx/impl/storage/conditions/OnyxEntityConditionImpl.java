package org.classfoo.onyx.impl.storage.conditions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.storage.conditions.OnyxEntityCondition;
import org.classfoo.onyx.api.storage.conditions.OnyxEntityConditionListener;

/**
 * @see OnyxEntityCondition
 * @author ClassFoo
 *
 */
public class OnyxEntityConditionImpl implements OnyxEntityCondition {

	private String[] labels;

	private Map<String, String> conditions = new HashMap<String, String>(10);

	private OnyxEntityConditionListener listener;

	@Override
	public void setLabels(String... labels) {
		this.labels = labels;
	}

	@Override
	public void addCondition(String property, String value) {
		this.conditions.put(property, value);
	}

	@Override
	public void setListener(OnyxEntityConditionListener listener) {
		this.listener = listener;
	}

	@Override
	public void check(Map<String, Object> entity, OnyxStorageSession session) {
		for (String key : this.conditions.keySet()) {
			String value = this.conditions.get(key);
			String entityValue = MapUtils.getString(entity, key);
			if (!StringUtils.equals(value, entityValue)) {
				return;
			}
		}
		this.listener.onMatch(entity, session);
	}
}
