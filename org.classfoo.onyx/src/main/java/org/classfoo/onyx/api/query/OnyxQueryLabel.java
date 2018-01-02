package org.classfoo.onyx.api.query;

import java.util.Map;

/**
 * Onyx Query Label
 * @author ClassFoo
 *
 */
public interface OnyxQueryLabel extends OnyxQuerySingle<Map<String, Object>> {

    /**
     * set base id
     * @param kid
     */
    public void setKid(String kid);

    /**
     * set label name
     * @param name
     */
    public void setName(String name);

}
