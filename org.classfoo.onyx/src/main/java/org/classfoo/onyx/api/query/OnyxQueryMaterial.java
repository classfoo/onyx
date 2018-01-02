package org.classfoo.onyx.api.query;

import java.util.Map;

/**
 * Query Material
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryMaterial extends OnyxQuerySingle<Map<String, Object>> {

    /**
     * set material id
     * @param mid
     */
    public void setMid(String mid);

}
