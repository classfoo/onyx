package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxOperateAddLabel
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxOperateAddLabelImpl extends AbstractOnyxOperate implements OnyxOperateAddLabel {

    private String labelName;

    private List<String> parents;

    private List<String> links;

    private List<String> properties;

    private String kid;

    public OnyxOperateAddLabelImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setKnowledgeBase(String kid) {
        this.kid = kid;
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
        return session.createLabel(this.kid, this.labelName, this.parents, this.links, this.properties);
    }

}
