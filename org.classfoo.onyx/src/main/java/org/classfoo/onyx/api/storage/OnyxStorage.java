package org.classfoo.onyx.api.storage;

import java.util.Map;

import org.classfoo.onyx.api.storage.conditions.OnyxEntityCondition;

/**
 * Onyx Storage
 * @author ClassFoo
 *
 */
public interface OnyxStorage {

	/**
	 * open onyx storage session
	 * @return
	 */
	public OnyxStorageSession openSession();

	public OnyxEntityCondition createEntityCondition();

	public void addEntityCondition(OnyxEntityCondition condition);

	public void checkEntityConditions(Map<String, Object> entity, OnyxStorageSession session);

}
