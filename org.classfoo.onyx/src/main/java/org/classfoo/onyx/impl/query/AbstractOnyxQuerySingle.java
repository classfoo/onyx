package org.classfoo.onyx.impl.query;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQuerySingle;

/**
 * @see OnyxQuerySingle
 * @author ClassFoo
 * @createdate 20180102
 */
public abstract class AbstractOnyxQuerySingle<T> extends AbstractOnyxQuery implements OnyxQuerySingle<T> {

    public AbstractOnyxQuerySingle(OnyxService onyxService) {
        super(onyxService);
    }

}
