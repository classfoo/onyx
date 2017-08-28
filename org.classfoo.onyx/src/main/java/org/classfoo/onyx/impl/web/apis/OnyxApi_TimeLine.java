package org.classfoo.onyx.impl.web.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
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
		OnyxQueryKnowledgeBases queryknowledgeBases = this.onyxService.createQuery(OnyxQueryKnowledgeBases.class);
		List<Map<String, Object>> bases = queryknowledgeBases.queryList();
		ArrayList<Map<String,Object>> result = new ArrayList<Map<String,Object>>(100);
		for (Map<String, Object> base : bases) {
			base.put("type", "base");
			result.add(base);
			String kid = MapUtils.getString(base, "kid");
			OnyxQueryEntities queryEntities = this.onyxService.createQuery(OnyxQueryEntities.class);
			queryEntities.setKid(kid);
			List<Map<String, Object>> entities = queryEntities.queryList();
			for(Map<String, Object> entity:entities){
				int r = (int) (Math.random() * 255);
				int g = (int) (Math.random() * 255);
				int b = (int) (Math.random() * 255);
				entity.put("color", "rgb(" + r + "," + g + "," + b + ")");
				entity.put("type", "entity");
				result.add(entity);
			}
		}
		return result;
	}

}
