package org.classfoo.onyx.impl.query;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

public class OnyxQueryKnowledgeBasesImpl extends OnyxQueryListImpl<Map<String, Object>> implements OnyxQueryKnowledgeBases {

	public OnyxQueryKnowledgeBasesImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public List<Map<String, Object>> queryList(long limit) {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		OnyxStorageSession session = storage.openSession();
		try {
			return session.queryBases();
		}
		finally {
			session.close();
		}
	}

}
