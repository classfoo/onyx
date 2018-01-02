package org.classfoo.onyx.api.operate;

import java.util.List;

/**
 * update label
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateUpdateLabel extends OnyxOperate {

    /**
     * set Knowledge Base id
     * @param kid
     */
    void setKnowledgeBase(String kid);

    /**
     * set Label id
     * @param lid
     */
    void setLabelId(String lid);

    /**
     * set Label name
     * @param name
     */
    void setLabelName(String name);

    /**
     * set Label Parents
     * @param parents
     */
    void setParents(List<String> parents);

    /**
     * set Label links
     * @param links
     */
    void setLinks(List<String> links);

    /**
     * set Label properties
     * @param properties
     */
    void setProperties(List<String> properties);

}
