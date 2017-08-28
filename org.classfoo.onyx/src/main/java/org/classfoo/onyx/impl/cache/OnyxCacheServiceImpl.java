package org.classfoo.onyx.impl.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.classfoo.onyx.api.cache.OnyxCacheQuery;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.springframework.stereotype.Component;

/**
 * @see OnyxCacheService
 * @author ClassFoo
 *
 */
@Component
public class OnyxCacheServiceImpl implements OnyxCacheService {

	private ConcurrentHashMap<Object, Object> cache = new ConcurrentHashMap<Object, Object>(10000);

	@Override
	public <T> T query(Object key, OnyxCacheQuery<T> onyxCacheQuery) {
//		Object value = cache.get(key);
//		if (value != null) {
//			return (T) value;
//		}
		T result = onyxCacheQuery.query();
		cache.put(key, result);
		return result;
	}

}
