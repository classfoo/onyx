package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

/**
 * update entity
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateUpdateEntity extends OnyxOperate {

    /**
     * set Knowledge Base id
     * @param kid
     */
    void setKnowledgeBase(String kid);

    /**
     * set Entity id
     * @param eid
     */
    void setEntityId(String eid);

    /**
     * set Entity modifies
     * @param modifies
     */
    void setModifies(List<Map<String, Object>> modifies);
}
