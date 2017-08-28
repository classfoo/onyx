package org.classfoo.onyx.api.query;

import org.classfoo.onyx.api.OnyxService;

public interface OnyxQueryService {
	public <T extends OnyxQuery> T createQuery(OnyxService onyxService, Class<T> type);
}
