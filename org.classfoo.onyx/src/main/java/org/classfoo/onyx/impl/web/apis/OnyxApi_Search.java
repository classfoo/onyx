package org.classfoo.onyx.impl.web.apis;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
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
		if ("all".equals(type)) {
			return this.searchAll(args);
		}
		if ("entity".equals(type)) {
			String text = MapUtils.getString(args, "text");
			OnyxIndexService indexService = this.onyxService.getIndexService();
			OnyxIndexSession session = indexService.openSession();
			try {
				List<Map<String, Object>> result = session.searchEntities(text);
				return result;
			}
			finally {
				session.close();
			}
		}
		if ("label".equals(type)) {
			String text = MapUtils.getString(args, "text");
			OnyxIndexService indexService = this.onyxService.getIndexService();
			OnyxIndexSession session = indexService.openSession();
			try {
				List<Map<String, Object>> result = session.searchLabels(text);
				return result;
			}
			finally {
				session.close();
			}
		}
		return null;
	}

	private Object searchAll(Map<String, Object> args) {
		String text = MapUtils.getString(args, "text");
		OnyxIndexService indexService = this.onyxService.getIndexService();
		OnyxIndexSession session = indexService.openSession();
		try {
			return session.searchEntities(text);
		}
		finally {
			session.close();
		}
	}
}
