package org.classfoo.onyx.api.index;

/**
 * Onyx Index Service
 * @author ClassFoo
 *
 */
public interface OnyxIndexService {

    /**
     * open onyx index session
     * @return
     */
    public OnyxIndexSession openSession();

    /**
     * get onyx indexing thread
     * @return
     */
    public OnyxIndexThread getIndexThread();

}
