package org.classfoo.onyx.api.storage;

/**
 * Onyx Storage
 * @author ClassFoo
 *
 */
public interface OnyxStorage {

    /**
     * open onyx storage session
     * @return
     */
    public OnyxStorageSession openSession();

}
