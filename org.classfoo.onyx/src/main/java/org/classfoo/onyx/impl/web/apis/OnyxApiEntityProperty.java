package org.classfoo.onyx.impl.web.apis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Onyx Entity Property Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiEntityProperty extends AbstractOnyxApi implements OnyxApi {

    private static final String ENTITYPROPERTY = "entityproperty";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return ENTITYPROPERTY;
    }

    @Override
    public Object post(Map<String, Object> args) {
        String id = MapUtils.getString(args, "id");
        String name = MapUtils.getString(args, "name");
        String value = MapUtils.getString(args, "value");
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            HashMap<String, Object> properties = new HashMap<String, Object>(2);
            properties.put(name, value);
            return session.addEntityProperties(id, properties);
        }
        finally {
            session.close();
        }
    }
}
