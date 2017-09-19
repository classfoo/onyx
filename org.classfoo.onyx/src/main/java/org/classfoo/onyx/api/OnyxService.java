package org.classfoo.onyx.api;

import java.util.Map;

import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.file.OnyxFileService;
import org.classfoo.onyx.api.operate.OnyxOperate;
import org.classfoo.onyx.api.query.OnyxQuery;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.streaming.OnyxStreamingService;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Onyx Service
 * @author ClassFoo
 *
 */
public interface OnyxService {

	/**
	 * create query object
	 * @param type
	 * @return
	 */
	public <T extends OnyxQuery> T createQuery(Class<T> type);

	/**
	 * create operate object
	 * @param type
	 * @return
	 */
	public <T extends OnyxOperate> T createOperate(Class<T> type);

	/**
	 * Handle Web Api Request
	 * @param method 
	 * @param args
	 * @return
	 */
	public Object service(RequestMethod method, Map<String, Object> args);

	/**
	 * get onyx storage service
	 * @return
	 */
	public OnyxStorageService getStorageService();

	/**
	 * get onyx cache service
	 * @return
	 */
	public OnyxCacheService getCacheService();

	/**
	 * get onyx file service
	 * @return
	 */
	public OnyxFileService getFileService();

	/**
	 * get onyx streaming service
	 * @return
	 */
	public OnyxStreamingService getStreamingService();
}
