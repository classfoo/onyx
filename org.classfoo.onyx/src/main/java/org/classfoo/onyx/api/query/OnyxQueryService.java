package org.classfoo.onyx.api.query;

import org.classfoo.onyx.api.OnyxService;

/**
 * Query Service
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryService {

    /**
     * create new query
     * @param onyxService
     * @param type
     * @return
     */
    public <T extends OnyxQuery> T createQuery(OnyxService onyxService, Class<T> type);
}
