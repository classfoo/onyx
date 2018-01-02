package org.classfoo.onyx.impl.web.apis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.operate.OnyxOperateSaveLabel;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateLabel;
import org.classfoo.onyx.api.query.OnyxQueryLabel;
import org.classfoo.onyx.api.query.OnyxQueryLabels;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jodd.util.HtmlEncoder;

/**
 * KnowledgeBases Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiLabel extends AbstractOnyxApi implements OnyxApi {

    private static final String LABEL = "label";

    @Autowired
    private OnyxService onyxService;

    @Override
    public String getResource() {
        return LABEL;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) throws UnsupportedEncodingException {
        OnyxQueryLabel queryLabel = onyxService.createQuery(OnyxQueryLabel.class);
        String kid = MapUtils.getString(args, "kid");
        queryLabel.setKid(kid);
        String name = URLDecoder.decode(resid, "UTF-8");
        queryLabel.setName(name);
        return queryLabel.querySingle();
    }

    @Override
    public Object getList(Map<String, Object> args) {
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String kid = MapUtils.getString(args, "kid");
            List<Map<String, Object>> labels = session.queryBaseLabels(kid);
            for (Map<String, Object> label : labels) {
                label.put("type", "label");
            }
            return labels;
        }
        finally {
            session.close();
        }
    }

    @Override
    public Object post(Map<String, Object> args) {
        String kid = OnyxUtils.readJson(args, "kid", String.class);
        String name = OnyxUtils.readJson(args, "name", String.class);
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            String lid = OnyxUtils.getRandomUUID("l");
            return session.addLabel(kid, lid, name, null);
        }
        finally {
            session.close();
        }
    }

}
