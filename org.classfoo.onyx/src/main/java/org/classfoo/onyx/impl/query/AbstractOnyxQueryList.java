package org.classfoo.onyx.impl.query;

import java.util.List;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryList;

/**
 * @see OnyxQueryList
 * @author ClassFoo
 * @createdate 20180102
 */
public abstract class AbstractOnyxQueryList<T> extends AbstractOnyxQuery implements OnyxQueryList<T> {

    public AbstractOnyxQueryList(OnyxService onyxService) {
        super(onyxService);
    }

    @Override
    public List<T> queryList() {
        return this.queryList(-1);
    }
}
