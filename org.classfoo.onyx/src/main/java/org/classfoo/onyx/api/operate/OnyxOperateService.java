package org.classfoo.onyx.api.operate;

import org.classfoo.onyx.api.OnyxService;

/**
 * Operate Service
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxOperateService {

    /**
     * create new operate
     * @param onyxService
     * @param type
     * @return
     */
    public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type);
}
