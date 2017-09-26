package org.classfoo.onyx.impl.web.apis;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_LinkNames extends OnyxApiImpl implements OnyxApi {

	private static final String LINKNAMES = "linknames";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return LINKNAMES;
	}

	@Override
	public Object getSingle(String resid, Map<String, Object> args) {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		OnyxStorageSession session = storage.openSession();
		try {
			return session.queryLinkNames(resid);
		}
		finally {
			session.close();
		}
	}

	@Override
	public Object getList(Map<String, Object> args) {
		String kid = MapUtils.getString(args, "kid");
		OnyxQueryEntities queryEntities = this.onyxService.createQuery(OnyxQueryEntities.class);
		queryEntities.setKid(kid);
		return queryEntities.queryList();
	}
}
