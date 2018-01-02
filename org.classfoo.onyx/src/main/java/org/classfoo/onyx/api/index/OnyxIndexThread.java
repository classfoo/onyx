package org.classfoo.onyx.api.index;

import java.util.HashMap;
import java.util.Map;

/**
 * Onyx Indexing queue thread
 * @author ClassFoo
 *
 */
public interface OnyxIndexThread extends Runnable {

    public static final int INSERT = 0;

    public static final int UPDATE = 1;

    public static final int MERGE = 2;

    public static final int UPSERT = 3;

    /**
     * start the thread
     */
    public void start();

    /**
     * add index into thread queue
     * @param operate
     * @param domain
     * @param index
     * @param id
     * @param object
     */
    public void addIndex(int operate, String domain, String index, String id, Map<String, Object> object);

    /**
     * shutdown the thread
     */
    public void shutdown();

}
