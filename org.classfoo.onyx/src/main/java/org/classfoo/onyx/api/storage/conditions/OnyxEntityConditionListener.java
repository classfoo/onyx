package org.classfoo.onyx.api.storage.conditions;

import java.util.Map;

import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * Onyx Entity Condition Listener
 * @author ClassFoo
 *
 */
public interface OnyxEntityConditionListener {

	/**
	 * on entity match the condition
	 * @param entity
	 * @param session
	 */
	public void onMatch(Map<String, Object> entity, OnyxStorageSession session);
}
