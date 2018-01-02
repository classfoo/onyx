package org.classfoo.onyx.api.operate;

import java.util.Map;

/**
 * Onyx Operte
 * @author ClassFoo
 *
 */
public interface OnyxOperate {

    /**
     * commit the operate to storage
     * @return
     */
    public Map<String, Object> commit();

}
