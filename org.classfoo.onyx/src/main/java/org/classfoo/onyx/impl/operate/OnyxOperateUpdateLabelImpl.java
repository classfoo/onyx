package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateLabel;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxOperateUpdateLabel
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxOperateUpdateLabelImpl extends AbstractOnyxOperate implements OnyxOperateUpdateLabel {

    private String labelName;

    private List<String> parents;

    private List<String> links;

    private List<String> properties;

    private String kid;

    private String lid;

    public OnyxOperateUpdateLabelImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setKnowledgeBase(String kid) {
        this.kid = kid;
    }

    @Override
    public void setLabelId(String lid) {
        this.lid = lid;
    }

    @Override
    public void setLabelName(String name) {
        this.labelName = name;
    }

    @Override
    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    @Override
    public void setLinks(List<String> links) {
        this.links = links;
    }

    @Override
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Object> execute(OnyxStorageSession session) {
        return session.updateLabel(this.kid, this.lid, this.labelName, this.parents, this.links, this.properties);
    }

}
