package org.classfoo.onyx.impl.web.apis;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.index.OnyxIndexService;
import org.classfoo.onyx.api.index.OnyxIndexSession;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiEntity extends AbstractOnyxApi implements OnyxApi {

    private static final String ENTITY = "entity";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return ENTITY;
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
        OnyxIndexService indexService = this.onyxService.getIndexService();
        OnyxIndexSession session = indexService.openSession();
        try {
            return session.searchBaseEntities(kid, null);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Object post(Map<String, Object> args) {
        String kid = MapUtils.getString(args, "kid");
        String name = MapUtils.getString(args, "name");
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            return session.addEntity(kid, name, null, null);
        }
        finally {
            session.close();
        }
    }
}
