package org.classfoo.onyx.impl.web.apis;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateEntity;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Onyx Event Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiEvent extends AbstractOnyxApi implements OnyxApi {

    private static final String EVENT = "event";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return EVENT;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) {
        OnyxStorage storage = this.onyxService.getStorageService().getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            return session.queryEntityEvents(resid);
        }
        finally {
            session.close();
        }
    }
}
