package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

/**
 * Save Label
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateSaveLabel extends OnyxOperate {

    /**
     * set Knowledge Base id
     * @param kid
     */
    void setKid(String kid);

    /**
     * set Label id
     * @param lid
     */
    void setLid(String lid);

    /**
     * set Label name
     * @param name
     */
    void setName(String name);

    /**
     * set Label modifies
     * @param modifies
     */
    void setModifies(List<Map<String, Object>> modifies);

}
