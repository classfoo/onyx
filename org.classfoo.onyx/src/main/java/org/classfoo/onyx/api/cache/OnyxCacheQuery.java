package org.classfoo.onyx.api.cache;

/**
 * Onyx Query By Cache
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxCacheQuery<T> {

    /**
     * query cache
     * @return
     */
    public T query();
}
