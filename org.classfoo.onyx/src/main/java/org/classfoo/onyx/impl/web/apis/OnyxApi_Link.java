package org.classfoo.onyx.impl.web.apis;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateEntity;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_Link extends OnyxApiImpl implements OnyxApi {

	private static final String LINK = "link";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return LINK;
	}

	@Override
	public Object getSingle(String resid, Map<String, Object> args) {
		OnyxQueryEntity queryEntity = this.onyxService.createQuery(OnyxQueryEntity.class);
		queryEntity.setEid(resid);
		return queryEntity.querySingle();
	}

	@Override
	public Object getList(Map<String, Object> args) {
		String kid = MapUtils.getString(args, "kid");
		OnyxQueryEntities queryEntities = this.onyxService.createQuery(OnyxQueryEntities.class);
		queryEntities.setKid(kid);
		return queryEntities.queryList();
	}
}
