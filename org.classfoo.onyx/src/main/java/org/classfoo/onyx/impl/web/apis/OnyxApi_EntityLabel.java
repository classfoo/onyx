package org.classfoo.onyx.impl.web.apis;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Onyx Entity Label Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_EntityLabel extends OnyxApiImpl implements OnyxApi {

	private static final String ENTITYLABEL = "entitylabel";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return ENTITYLABEL;
	}

	@Override
	public Object post(Map<String, Object> args) {
		String eid = MapUtils.getString(args, "eid");
		String name = MapUtils.getString(args, "name");
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		OnyxStorageSession session = storage.openSession();
		try {
			return session.addEntityLabels(eid, Arrays.asList(name));
		}
		finally {
			session.close();
		}
	}
}
