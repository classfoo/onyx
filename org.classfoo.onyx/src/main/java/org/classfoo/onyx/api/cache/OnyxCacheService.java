package org.classfoo.onyx.api.cache;

public interface OnyxCacheService {

	public <T extends Object> T query(Object key, OnyxCacheQuery<T> onyxCacheQuery);

}
