package org.classfoo.onyx.api.storage.conditions;

import java.util.Map;

import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * Onyx Entity Condition
 * @author ClassFoo
 *
 */
public interface OnyxEntityCondition {

	/**
	 * set entity labels condition
	 * @param labels
	 */
	void setLabels(String... labels);

	/**
	 * add entity property condition
	 * @param property
	 * @param value
	 */
	void addCondition(String property, String value);

	/**
	 * set condition listener
	 * @param listener
	 */
	public void setListener(OnyxEntityConditionListener listener);

	/**
	 * check entity and fire event to listener
	 * @param entity
	 * @param session
	 */
	public void check(Map<String, Object> entity, OnyxStorageSession session);
}
