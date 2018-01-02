package org.classfoo.onyx.api.query;

import java.util.Map;

/**
 * Query Entities
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryEntities extends OnyxQueryList<Map<String, Object>> {

    /**
     * set knowledge base id
     * @param kid
     */
    public void setKid(String kid);
}
