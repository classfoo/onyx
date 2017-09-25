package org.classfoo.onyx.impl.storage;

import java.util.Map;
import java.util.Queue;

import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.storage.conditions.OnyxEntityCondition;
import org.classfoo.onyx.impl.storage.conditions.OnyxEntityConditionImpl;
import org.eclipse.jetty.util.ConcurrentArrayQueue;

/**
 * @see OnyxStorage
 * @author ClassFoo
 *
 */
public abstract class OnyxStorageImpl implements OnyxStorage {

	private Queue<OnyxEntityCondition> conditions = new ConcurrentArrayQueue<OnyxEntityCondition>();

	@Override
	public OnyxEntityCondition createEntityCondition() {
		return new OnyxEntityConditionImpl();
	}

	@Override
	public void addEntityCondition(OnyxEntityCondition condition) {
		this.conditions.add(condition);
	}

	@Override
	public void checkEntityConditions(Map<String, Object> entity, OnyxStorageSession session) {
		for (OnyxEntityCondition condition : this.conditions) {
			condition.check(entity, session);
		}
	}
}
