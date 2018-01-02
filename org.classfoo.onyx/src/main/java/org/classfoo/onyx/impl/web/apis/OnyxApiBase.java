package org.classfoo.onyx.impl.web.apis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBase;
import org.classfoo.onyx.api.query.OnyxQueryKnowledgeBases;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiBase extends AbstractOnyxApi implements OnyxApi {

    private static final String BASE = "base";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return BASE;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) {
        OnyxQueryKnowledgeBase queryBase = this.onyxService.createQuery(OnyxQueryKnowledgeBase.class);
        queryBase.setKid(resid);
        Map<String, Object> single = queryBase.querySingle();
        if (single == null) {
            HashMap<String, Object> map = new HashMap<String, Object>(8);
            map.put("kid", resid);
            map.put("icon", "icon-warning");
            map.put("caption", "Warning! Knowledge Base Not Exists!");
            return map;
        }
        return single;
    }

    @Override
    public Object getList(Map<String, Object> args) {
        OnyxQueryKnowledgeBases queryknowledgeBases = this.onyxService.createQuery(OnyxQueryKnowledgeBases.class);
        List<Map<String, Object>> bases = queryknowledgeBases.queryList();
        return bases;
    }

    @Override
    public Object post(Map<String, Object> args) throws IOException {
        String name = MapUtils.getString(args, "name");
        String caption = MapUtils.getString(args, "desc");
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String kid = OnyxUtils.getRandomUUID("k");
            return session.addBase(kid, name, caption);
        }
        finally {
            session.close();
        }
    }
}
