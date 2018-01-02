package org.classfoo.onyx.impl.web.apis;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiGraph extends AbstractOnyxApi implements OnyxApi {

    private static final String GRAPH = "graph";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return GRAPH;
    }

    @Override
    public Object getList(Map<String, Object> args) throws IOException {
        OnyxStorage storage = onyxService.getStorageService().getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String kid = MapUtils.getString(args, "kid");
            return session.queryGraphs();
        }
        finally {
            session.close();
        }
    }

    @Override
    public Object post(Map<String, Object> args) throws IOException {
        OnyxStorage storage = onyxService.getStorageService().getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String name = MapUtils.getString(args, "name");
            String kid = MapUtils.getString(args, "kid");
            String content = MapUtils.getString(args, "content");
            session.addGraph(name, kid, content, null);
            return null;
        }
        finally {
            session.close();
        }
    }
}
