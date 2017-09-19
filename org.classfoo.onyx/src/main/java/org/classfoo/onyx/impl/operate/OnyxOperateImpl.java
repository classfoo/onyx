package org.classfoo.onyx.impl.operate;

import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperate;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

public abstract class OnyxOperateImpl implements OnyxOperate {

	protected OnyxService onyxService;

	public OnyxOperateImpl(OnyxService onyxService) {
		this.onyxService = onyxService;
	}

	@Override
	public Map<String, Object> commit() {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		OnyxStorageSession session = storage.openSession();
		try {
			return this.execute(session);
		}
		finally {
			session.close();
		}
	}

	/**
	 * commit the operate to storage session
	 * @param session
	 * @return
	 */
	public abstract Map<String, Object> execute(OnyxStorageSession session);

}
