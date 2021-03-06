package org.classfoo.onyx.impl.operate;

import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddMaterial;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxOperateAddMaterial
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxOperateAddMaterialImpl extends AbstractOnyxOperate implements OnyxOperateAddMaterial {

    private String kid;

    private Map<String, Object> properties;

    private String name;

    private String desc;

    public OnyxOperateAddMaterialImpl(OnyxService onyxService) {
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
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Object> execute(OnyxStorageSession session) {
        return session.addMaterial(this.name, this.desc, this.kid, this.properties);
    }

}
