package org.classfoo.onyx.impl.query;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.cache.OnyxCacheQuery;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.query.OnyxQueryLabels;
import org.classfoo.onyx.api.query.OnyxQueryMaterials;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;

public class OnyxQueryMaterialsImpl extends OnyxQueryListImpl<Map<String, Object>> implements OnyxQueryMaterials {

	private String kid;

	public OnyxQueryMaterialsImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKid(String kid) {
		this.kid = kid;
	}

	@Override
	public int hashCode() {
		return this.kid == null ? -1 : this.kid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OnyxQueryMaterialsImpl)) {
			return false;
		}
		OnyxQueryMaterialsImpl queryLabels = (OnyxQueryMaterialsImpl) obj;
		return StringUtils.equals(this.kid, queryLabels.kid);
	}

	@Override
	public List<Map<String, Object>> queryList(long limit) {
		OnyxCacheService cacheService = onyxService.getCacheService();
		List<Map<String, Object>> values = cacheService.query(this, new OnyxCacheQuery<List<Map<String, Object>>>() {
			public List<Map<String, Object>> query() {
				OnyxStorageService storageService = onyxService.getStorageService();
				OnyxStorage storage = storageService.getStorage();
				List<Map<String, Object>> labels = storage.queryMaterials(kid);
				return labels;
			}
		});
		return values;
	}

}
