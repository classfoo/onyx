package org.classfoo.onyx.api.operate;

import java.util.List;

/**
 * Onyx Operate Add Label
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateAddLabel extends OnyxOperate {

    /**
     * set Knowledge Base id
     * @param kid
     */
    void setKnowledgeBase(String kid);

    /**
     * set Label Name
     * @param name
     */
    void setLabelName(String name);

    /**
     * set Label Parents
     * @param parents
     */
    void setParents(List<String> parents);

    /**
     * set Label Links
     * @param links
     */
    void setLinks(List<String> links);

    /**
     * set Label Properties
     * @param properties
     */
    void setProperties(List<String> properties);

}
