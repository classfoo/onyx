package org.classfoo.onyx.impl.web.apis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateEntity;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiLink extends AbstractOnyxApi implements OnyxApi {

    private static final String LINK = "link";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return LINK;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) {
        OnyxQueryEntity queryEntity = this.onyxService.createQuery(OnyxQueryEntity.class);
        queryEntity.setEid(resid);
        return queryEntity.querySingle();
    }

    @Override
    public Object getList(Map<String, Object> args) {
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String id = MapUtils.getString(args, "id");
            return session.queryLinks(id);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Object post(Map<String, Object> args) throws IOException {
        String linkstr = MapUtils.getString(args, "links");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> links = mapper.readValue(linkstr, List.class);
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            for (Map<String, Object> link : links) {
                String name = MapUtils.getString(link, "name");
                Map<String, Object> source = (Map<String, Object>) MapUtils.getObject(link, "source");
                Map<String, Object> target = (Map<String, Object>) MapUtils.getObject(link, "target");
                String sourceid = MapUtils.getString(source, "id");
                String sourcename = MapUtils.getString(source, "name");
                String targetid = MapUtils.getString(target, "id");
                String targetname = MapUtils.getString(target, "name");
                session.addLink(name, sourceid, sourcename, targetid, targetname, null);
            }
        }
        finally {
            session.close();
        }
        return super.post(args);
    }
}
