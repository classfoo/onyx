package org.classfoo.onyx.api.cache;

/**
 * Onyx Cache Service
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxCacheService {

    /**
     * query cache
     * @param key
     * @param onyxCacheQuery
     * @return
     */
    public <T extends Object> T query(Object key, OnyxCacheQuery<T> onyxCacheQuery);

}
