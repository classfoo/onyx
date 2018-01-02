package org.classfoo.onyx.api.query;

import java.util.List;

/**
 * Query List
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQueryList<T> extends OnyxQuery {

    /**
     * fetch List values
     * @return
     */
    public List<T> queryList();

    /**
     * fetch list values with limit
     * @param limit
     * @return
     */
    public List<T> queryList(long limit);

}
