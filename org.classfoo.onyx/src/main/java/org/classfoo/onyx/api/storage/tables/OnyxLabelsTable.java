package org.classfoo.onyx.api.storage.tables;

/**
 * Labels Table
 * @author ClassFoo
 * @createdate 20180102
 */
public interface OnyxLabelsTable {

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

    /**
     * get knowledge base
     * @return
     */
    public String getKnowledgeBase();

    /**
     * set knowledge base
     * @param knowledgeBase
     */
    public void setKnowledgeBase(String knowledgeBase);
}
