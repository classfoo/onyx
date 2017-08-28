package org.classfoo.onyx.impl.query;

import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBase;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;

public class OnyxQueryKnowledgeBaseImpl extends OnyxQuerySingleImpl<Map<String, Object>> implements OnyxQueryKnowledgeBase {

	private String kid;

	public OnyxQueryKnowledgeBaseImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKid(String kid) {
		this.kid = kid;
	}

	@Override
	public Map<String, Object> querySingle() {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		return storage.queryBase(kid);
	}

}
