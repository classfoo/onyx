package org.classfoo.onyx.impl.query;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.cache.OnyxCacheQuery;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.query.OnyxQueryMaterial;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

public class OnyxQueryMaterialImpl extends OnyxQuerySingleImpl<Map<String, Object>> implements OnyxQueryMaterial {

	private String mid;

	public OnyxQueryMaterialImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setMid(String mid) {
		this.mid = mid;
	}

	@Override
	public int hashCode() {
		return this.mid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OnyxQueryMaterialImpl)) {
			return false;
		}
		OnyxQueryMaterialImpl queryMaterial = (OnyxQueryMaterialImpl) obj;
		if (!StringUtils.equals(this.mid, queryMaterial.mid)) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> querySingle() {
		OnyxCacheService cacheService = this.onyxService.getCacheService();
		Map<String, Object> result = cacheService.query(this, new OnyxCacheQuery<Map<String, Object>>() {
			@Override
			public Map<String, Object> query() {
				OnyxStorageService storageService = onyxService.getStorageService();
				OnyxStorage storage = storageService.getStorage();
				OnyxStorageSession session = storage.openSession();
				try {
					return session.queryMaterial(mid);
				}
				finally {
					session.close();
				}
			}
		});
		return result;
	}
}
