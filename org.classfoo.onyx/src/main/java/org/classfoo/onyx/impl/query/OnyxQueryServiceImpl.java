package org.classfoo.onyx.impl.query;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQuery;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBase;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
import org.classfoo.onyx.api.query.OnyxQueryLabel;
import org.classfoo.onyx.api.query.OnyxQueryLabels;
import org.classfoo.onyx.api.query.OnyxQueryMaterial;
import org.classfoo.onyx.api.query.OnyxQueryMaterials;
import org.classfoo.onyx.api.query.OnyxQueryService;
import org.springframework.stereotype.Component;

@Component
public class OnyxQueryServiceImpl implements OnyxQueryService {

	private static final Map<Class, Class> map = new HashMap<Class, Class>(100);

	static {
		map.put(OnyxQueryKnowledgeBase.class, OnyxQueryKnowledgeBaseImpl.class);
		map.put(OnyxQueryKnowledgeBases.class, OnyxQueryKnowledgeBasesImpl.class);
		map.put(OnyxQueryEntity.class, OnyxQueryEntityImpl.class);
		map.put(OnyxQueryEntities.class, OnyxQueryEntitiesImpl.class);
		map.put(OnyxQueryLabel.class, OnyxQueryLabelImpl.class);
		map.put(OnyxQueryLabels.class, OnyxQueryLabelsImpl.class);
		map.put(OnyxQueryMaterial.class, OnyxQueryMaterialImpl.class);
		map.put(OnyxQueryMaterials.class, OnyxQueryMaterialsImpl.class);
	}

	@Override
	public <T extends OnyxQuery> T createQuery(OnyxService onyxService, Class<T> type) {
		Class<T> query = map.get(type);
		if (query == null) {
			throw new RuntimeException();
		}
		try {
			Constructor<T> constructor = query.getConstructor(OnyxService.class);
			return constructor.newInstance(onyxService);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
