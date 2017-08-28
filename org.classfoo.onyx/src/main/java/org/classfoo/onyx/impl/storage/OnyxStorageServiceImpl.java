package org.classfoo.onyx.impl.storage;

import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.impl.storage.cassandra.OnyxStorage_Cassandra;
import org.springframework.stereotype.Component;

/**
 * @see OnyxStorageService
 * @author ClassFoo
 *
 */
@Component
public class OnyxStorageServiceImpl implements OnyxStorageService {

	OnyxStorage_Cassandra storage = new OnyxStorage_Cassandra();

	@Override
	public OnyxStorage getStorage() {
		return storage;
	}

}
