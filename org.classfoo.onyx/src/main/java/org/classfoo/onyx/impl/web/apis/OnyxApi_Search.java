package org.classfoo.onyx.impl.web.apis;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
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
public class OnyxApi_Search extends OnyxApiImpl implements OnyxApi {

	private static final String SEARCH = "search";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return SEARCH;
	}

	@Override
	public Object getSingle(String resid, Map<String, Object> args) {
		OnyxQueryEntity queryEntity = this.onyxService.createQuery(OnyxQueryEntity.class);
		queryEntity.setEid(resid);
		return queryEntity.querySingle();
	}

	@Override
	public Object getList(Map<String, Object> args) {
		String type = MapUtils.getString(args, "type");
		String text = MapUtils.getString(args, "text");
		List<Map<String, Object>> result = this.onyxService.getIndexService().searchEntity(text);
		return result;
	}
}