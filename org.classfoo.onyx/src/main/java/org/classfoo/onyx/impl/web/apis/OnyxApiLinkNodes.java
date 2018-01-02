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
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiLinkNodes extends AbstractOnyxApi implements OnyxApi {

    private static final String LINKNODES = "linknodes";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return LINKNODES;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) {
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            return session.queryLinkNodes(resid, args);
        }
        finally {
            session.close();
        }
    }
}
