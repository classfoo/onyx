package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

/**
 * Onyx Operate Add Entity
 * @author ClassFoo
 *
 */
public interface OnyxOperateAddEntity extends OnyxOperate {

    /**
     * set knowledge base id
     * @param kid
     */
    void setKnowledgeBase(String kid);

    /**
     * set entity name
     * @param name
     */
    void setName(String name);

    /**
     * set entity labels
     * @param labels
     */
    void setLabels(List<String> labels);

    /**
     * set entity properties
     * @param properties
     */
    void setProperties(Map<String, Object> properties);

}
