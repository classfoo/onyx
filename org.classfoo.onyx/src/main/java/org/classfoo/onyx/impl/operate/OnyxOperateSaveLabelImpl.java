package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateSaveLabel;
import org.classfoo.onyx.api.storage.OnyxStorageSession;
import org.classfoo.onyx.impl.OnyxUtils;

/**
 * @see OnyxOperateSaveLabel
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxOperateSaveLabelImpl extends AbstractOnyxOperate implements OnyxOperateSaveLabel {

    private String labelName;

    private String lid;

    private String kid;

    private List<Map<String, Object>> modifies;

    public OnyxOperateSaveLabelImpl(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public void setKid(String kid) {
        this.kid = kid;
    }

    @Override
    public void setLid(String lid) {
        this.lid = lid;
    }

    @Override
    public void setName(String name) {
        this.labelName = name;
    }

    @Override
    public void setModifies(List<Map<String, Object>> modifies) {
        this.modifies = modifies;
    }

    @Override
    public Map<String, Object> execute(OnyxStorageSession session) {
        if (StringUtils.isBlank(lid)) {
            return this.commitCreate(session);
        }
        else {
            return this.commitSave(session);
        }
    }

    private Map<String, Object> commitSave(OnyxStorageSession session) {
        return session.saveLabelModifies(this.kid, this.lid, this.labelName, modifies);
    }

    private Map<String, Object> commitCreate(OnyxStorageSession session) {
        String lid = OnyxUtils.getRandomUUID("l");
        return session.saveLabelModifies(this.kid, lid, this.labelName, modifies);
    }

}
