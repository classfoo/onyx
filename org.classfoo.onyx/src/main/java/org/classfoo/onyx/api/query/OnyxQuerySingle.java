package org.classfoo.onyx.api.query;

/**
 * Query Single
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxQuerySingle<T> extends OnyxQuery {

    /**
     * fetch single query result
     * @return
     */
    T querySingle();
}
