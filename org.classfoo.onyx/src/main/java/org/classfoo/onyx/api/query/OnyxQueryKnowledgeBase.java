package org.classfoo.onyx.api.query;

import java.util.Map;

/**
 * Query Knowledge Base
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryKnowledgeBase extends OnyxQuerySingle<Map<String, Object>> {

    /**
     * set Knowledge Base id
     * @param kid
     */
    public void setKid(String kid);
}
