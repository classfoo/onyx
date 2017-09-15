package org.classfoo.onyx.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.file.OnyxFileService;
import org.classfoo.onyx.api.operate.OnyxOperate;
import org.classfoo.onyx.api.operate.OnyxOperateService;
import org.classfoo.onyx.api.query.OnyxQuery;
import org.classfoo.onyx.api.query.OnyxQueryService;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.web.OnyxApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @see OnyxService
 * @author ClassFoo
 *
 */
@Component
public class OnyxServiceImpl implements OnyxService, InitializingBean {

	@Autowired
	private OnyxApi[] onyxApis;

	@Autowired
	private OnyxStorageService storageService;

	@Autowired
	private OnyxCacheService cacheService;

	@Autowired
	private OnyxQueryService queryService;

	@Autowired
	private OnyxOperateService operateService;

	@Autowired
	private OnyxFileService fileService;

	private Map<String, OnyxApi> onyxApiMap;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.onyxApiMap = new HashMap<String, OnyxApi>(this.onyxApis.length);
		for (OnyxApi onyxApi : this.onyxApis) {
			String action = onyxApi.getResource();
			this.onyxApiMap.put(action, onyxApi);
		}
		//		JanusGraph graph = JanusGraphFactory.open("berkeleyje:/tmp/graph");
		//		JanusGraphManagement mgr = graph.openManagement();
	}

	@Override
	public <T extends OnyxOperate> T createOperate(Class<T> type) {
		return operateService.createOperate(this, type);
	}

	@Override
	public <T extends OnyxQuery> T createQuery(Class<T> type) {
		return queryService.createQuery(this, type);
	}

	@Override
	public Object service(RequestMethod method, Map<String, Object> args) {
		String resource = MapUtils.getString(args, "resource");
		OnyxApi api = this.onyxApiMap.get(resource);
		try {
			switch (method) {
				case DELETE:
					return api.delete(args);
				case GET:
					return api.get(args);
				case HEAD:
					return api.head(args);
				case OPTIONS:
					return api.options(args);
				case PATCH:
					return api.patch(args);
				case POST:
					return api.post(args);
				case PUT:
					return api.put(args);
				case TRACE:
					return api.trace(args);
			}
			return api.get(args);
		}
		catch (Exception e) {
			HashMap<String, Object> error = new HashMap<String, Object>(3);
			error.put("type", "error");
			error.put("message", ExceptionUtils.getMessage(e));
			error.put("details", ExceptionUtils.getFullStackTrace(e));
			return error;
		}
	}

	@Override
	public OnyxStorageService getStorageService() {
		return this.storageService;
	}

	@Override
	public OnyxCacheService getCacheService() {
		return this.cacheService;
	}

	@Override
	public OnyxFileService getFileService() {
		return this.fileService;
	}
}
