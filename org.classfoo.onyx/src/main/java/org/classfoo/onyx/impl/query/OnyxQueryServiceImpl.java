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

/**
 * @see OnyxQueryService
 * @author ClassFoo
 * @createdate 20180102
 */
@Component
public class OnyxQueryServiceImpl implements OnyxQueryService {

    private static final Map<Class, Class> MAP = new HashMap<Class, Class>(100);

    static {
        MAP.put(OnyxQueryKnowledgeBase.class, OnyxQueryKnowledgeBaseImpl.class);
        MAP.put(OnyxQueryKnowledgeBases.class, OnyxQueryKnowledgeBasesImpl.class);
        MAP.put(OnyxQueryEntity.class, OnyxQueryEntityImpl.class);
        MAP.put(OnyxQueryEntities.class, OnyxQueryEntitiesImpl.class);
        MAP.put(OnyxQueryLabel.class, OnyxQueryLabelImpl.class);
        MAP.put(OnyxQueryLabels.class, OnyxQueryLabelsImpl.class);
        MAP.put(OnyxQueryMaterial.class, OnyxQueryMaterialImpl.class);
        MAP.put(OnyxQueryMaterials.class, OnyxQueryMaterialsImpl.class);
    }

    @Override
    public <T extends OnyxQuery> T createQuery(OnyxService onyxService, Class<T> type) {
        Class<T> query = MAP.get(type);
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
