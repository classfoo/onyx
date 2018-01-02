package org.classfoo.onyx.api.base;

import java.util.Map;

/**
 * Knowledge Base
 * @author ClassFoo
 *
 */
public interface OnyxKnowledgeBase {

    /**
     * get knowledge base json
     * @return
     */
    public Map<String, Object> toJson();
}
