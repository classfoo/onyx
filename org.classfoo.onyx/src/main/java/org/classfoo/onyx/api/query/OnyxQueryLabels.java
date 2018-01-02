package org.classfoo.onyx.api.query;

import java.util.Map;

/**
 * Query Labels
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryLabels extends OnyxQueryList<Map<String, Object>> {

    /**
     * set knowledge base id
     * @param kid
     */
    public void setKid(String kid);
}
