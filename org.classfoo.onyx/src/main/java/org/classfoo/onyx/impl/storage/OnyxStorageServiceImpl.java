package org.classfoo.onyx.impl.storage;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.impl.storage.cassandra.OnyxStorage_Cassandra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @see OnyxStorageService
 * @author ClassFoo
 *
 */
@Component
public class OnyxStorageServiceImpl implements OnyxStorageService {

	@Autowired
	private OnyxService onyxService;

	private OnyxStorage_Cassandra storage = null;

	@Override
	public OnyxStorage getStorage() {
		if (storage == null) {
			storage = new OnyxStorage_Cassandra(onyxService);
		}
		return storage;
	}

}
