package org.classfoo.onyx.impl.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.cache.OnyxCacheQuery;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.query.OnyxQueryLabel;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxQueryLabel
 * @author ClassFoo
 *
 */
public class OnyxQueryLabelImpl extends AbstractOnyxQuerySingle<Map<String, Object>> implements OnyxQueryLabel {

    private static final String PROPERTIES = "properties";

    private static final String UPDATE = "update";

    private static final String DELETE = "delete";

    private static final String CREATE = "create";

    private static final String CAPTION = "caption";

    private static final String NAME = "name";

    private static final String PARENTS = "parents";

    private static final String LID = "lid";

    private String kid;

    private String name;

    public OnyxQueryLabelImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setKid(String kid) {
        this.kid = kid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.kid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OnyxQueryLabelImpl)) {
            return false;
        }
        OnyxQueryLabelImpl queryLabel = (OnyxQueryLabelImpl) obj;
        if (!StringUtils.equals(this.kid, queryLabel.kid)) {
            return false;
        }
        if (!StringUtils.equals(this.name, queryLabel.name)) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> querySingle() {
        OnyxCacheService cacheService = this.onyxService.getCacheService();
        Map<String, Object> result = cacheService.query(this, new OnyxCacheQuery<Map<String, Object>>() {
            @Override
            public Map<String, Object> query() {
                OnyxStorageService storageService = onyxService.getStorageService();
                OnyxStorage storage = storageService.getStorage();
                OnyxStorageSession session = storage.openSession();
                try {
                    return session.queryBaseLabel(kid, name);
                }
                finally {
                    session.close();
                }
            }
        });
        return result;
    }

    private Map<String, Object> convertToLabel(List<Map<String, Object>> modifies) {
        HashMap<String, Object> label = new HashMap<String, Object>(10);
        for (Map<String, Object> modify : modifies) {
            this.modifyLabel(label, modify);
        }
        return label;
    }

    private void modifyLabel(Map<String, Object> label, Map<String, Object> modify) {
        String lid = MapUtils.getString(modify, LID);
        String kid = MapUtils.getString(modify, "kid");
        label.put(LID, lid);
        label.put("kid", kid);
        String key = MapUtils.getString(modify, "key");
        if (NAME.equals(key)) {
            String name = MapUtils.getString(modify, NAME);
            label.put(NAME, name);
        }
        else if (PARENTS.equals(key)) {
            String operate = MapUtils.getString(modify, "operate");
            if (CREATE.equals(operate)) {
                String parent = MapUtils.getString(modify, "parent");
                List<Map<String, Object>> parents = (List<Map<String, Object>>) label.get(PARENTS);
                if (parents == null) {
                    parents = new ArrayList<Map<String, Object>>(10);
                    label.put(PARENTS, parents);
                }
                HashMap<String, Object> item = new HashMap<String, Object>(3);
                item.put(NAME, parent);
                item.put(CAPTION, parent);
                parents.add(item);
            }
            else if (DELETE.equals(operate)) {

            }
        }
        else if (PROPERTIES.equals(key)) {
            String operate = MapUtils.getString(modify, "operate");
            if (CREATE.equals(operate)) {
                String pname = MapUtils.getString(modify, "pname");
                String ptype = MapUtils.getString(modify, "ptype");
                List<Map<String, Object>> properties = (List<Map<String, Object>>) label.get(PROPERTIES);
                if (properties == null) {
                    properties = new ArrayList<Map<String, Object>>(10);
                    label.put(PROPERTIES, properties);
                }
                HashMap<String, Object> property = new HashMap<String, Object>(3);
                property.put(NAME, pname);
                property.put("type", ptype);
                properties.add(property);
            }
            else if (UPDATE.equals(operate)) {
                String pname = MapUtils.getString(modify, "pname");
                String ptype = MapUtils.getString(modify, "ptype");
                List<Map<String, Object>> properties = (List<Map<String, Object>>) label.get(PROPERTIES);
                for (Map<String, Object> property : properties) {
                    if (StringUtils.equals(pname, MapUtils.getString(property, NAME))) {
                        property.put("type", ptype);
                    }
                }
            }
            else if (DELETE.equals(operate)) {

            }
        }
        else {

        }
    }
}
