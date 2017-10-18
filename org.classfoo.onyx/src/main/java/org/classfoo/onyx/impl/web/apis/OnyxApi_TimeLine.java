package org.classfoo.onyx.impl.web.apis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Timeline Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_TimeLine extends OnyxApiImpl implements OnyxApi {

	private static final String TIMELINE = "timeline";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return TIMELINE;
	}

	@Override
	public Object get(Map<String, Object> args) {
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(100);
		OnyxStorage storage = this.onyxService.getStorageService().getStorage();
		OnyxStorageSession session = storage.openSession();
		try {
			List<Map<String, Object>> bases = session.queryBases();
			for (Map<String, Object> base : bases) {
				base.put("type", "base");
				result.add(base);
				String kid = MapUtils.getString(base, "id");
				List<Map<String, Object>> graphs = session.queryBaseGraphs(kid);
				for(Map<String, Object> graph:graphs){
					graph.put("color", OnyxUtils.getRandomColor());
					graph.put("type", "graph");
					result.add(graph);
				}
				List<Map<String, Object>> entities = session.queryBaseEntities(kid);
				for (Map<String, Object> entity : entities) {
					entity.put("color", OnyxUtils.getRandomColor());
					entity.put("type", "entity");
					result.add(entity);
				}
			}
		}
		finally {
			session.close();
		}

		return result;
	}

}
