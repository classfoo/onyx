package org.classfoo.onyx.api.operate;

import java.util.Map;

/**
 * Add Material
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateAddMaterial extends OnyxOperate {

    /**
     * set Knowledge Base id
     * @param kid
     */
    void setKid(String kid);

    /**
     * set Material name
     * @param name
     */
    void setName(String name);

    /**
     * set Material description
     * @param desc
     */
    void setDesc(String desc);

    /**
     * set Material properties
     * @param properties
     */
    void setProperties(Map<String, Object> properties);

}
