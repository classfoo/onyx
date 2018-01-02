package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddEntity;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxOperateAddEntity
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxOperateAddEntityImpl extends AbstractOnyxOperate implements OnyxOperateAddEntity {

    private String name;

    private Map<String, Object> properties;

    private String kid;

    private List<String> labels;

    public OnyxOperateAddEntityImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setKnowledgeBase(String kid) {
        this.kid = kid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Object> execute(OnyxStorageSession session) {
        return session.addEntity(this.kid, this.name, this.labels, this.properties);
    }

}
