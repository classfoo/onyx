package org.classfoo.onyx.api.storage.tables;

/**
 * Table
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxTable {

    /**
     * get uuid
     * @return
     */
    public String getUUID();

    /**
     * set uuid
     * @param uuid
     */
    public void setUUID(String uuid);

    /**
     * get name
     * @return
     */
    public String getName();

    /**
     * set name
     * @param name
     */
    public void setName(String name);
}
