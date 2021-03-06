package org.classfoo.onyx.impl.query;

import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxQueryEntity
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxQueryEntityImpl extends AbstractOnyxQuerySingle<Map<String, Object>> implements OnyxQueryEntity {

    private String eid;

    public OnyxQueryEntityImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setEid(String eid) {
        this.eid = eid;
    }

    @Override
    public Map<String, Object> querySingle() {
        OnyxStorageService storageService = this.onyxService.getStorageService();
        OnyxStorage storage = storageService.getStorage();
        OnyxStorageSession session = storage.openSession();
        try {
            Map<String, Object> entity = session.queryEntity(this.eid);
            return entity;
        }
        finally {
            session.close();
        }
    }

    //	private Map<String, Object> convertToEntity(List<Map<String, Object>> modifies) {
    //		Map<String, Object> entity = new HashMap<String, Object>(10);
    //		for (Map<String, Object> modify : modifies) {
    //			this.modifyEntity(entity, modify);
    //		}
    //		return entity;
    //	}
    //
    //	private void modifyEntity(Map<String, Object> entity, Map<String, Object> modify) {
    //		String eid = MapUtils.getString(modify, "eid");
    //		String kid = MapUtils.getString(modify, "kid");
    //		String name = MapUtils.getString(modify, "name");
    //		entity.put("eid", eid);
    //		entity.put("kid", kid);
    //		entity.put("name", name);
    //	}

}
