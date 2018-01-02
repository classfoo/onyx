package org.classfoo.onyx.impl.query;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQuery;

/**
 * @see OnyxQuery
 * @author ClassFoo
 * @createdate 20180102
 */
public abstract class AbstractOnyxQuery implements OnyxQuery {

    protected OnyxService onyxService;

    public AbstractOnyxQuery(OnyxService onyxService) {
        this.onyxService = onyxService;
    }
}
