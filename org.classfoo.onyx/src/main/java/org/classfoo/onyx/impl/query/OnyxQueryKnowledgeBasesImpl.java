package org.classfoo.onyx.impl.query;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;

public class OnyxQueryKnowledgeBasesImpl extends OnyxQueryListImpl<Map<String, Object>> implements OnyxQueryKnowledgeBases {

	public OnyxQueryKnowledgeBasesImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public List<Map<String, Object>> queryList(long limit) {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		return storage.queryBases();
	}

}
