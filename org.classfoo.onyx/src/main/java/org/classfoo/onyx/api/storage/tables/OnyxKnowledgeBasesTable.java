package org.classfoo.onyx.api.storage.tables;

/**
 * Onyx Knowledge Base Table
 * @author ClassFoo
 *
 */
public interface OnyxKnowledgeBasesTable extends OnyxTable {

    /**
     * get knowledge base description
     * @return
     */
    public String getDescription();

    /**
     * set knowledge base description
     * @param description
     */
    public void setDescription(String description);

    /**
     * get knowledge base category
     * @return
     */
    public String getCategory();

    /**
     * set knowledge base category
     * @param category
     */
    public void setCategory(String category);

    /**
     * get knowledge base creator
     * @return
     */
    public String getCreator();

    /**
     * set knowledge base creator
     * @param creator
     */
    public void setCreator(String creator);

    /**
     * get knowledge base create time
     * @return
     */
    public long getCreateTime();

    /**
     * set knowledge base create time
     * @param createTime
     */
    public void setCreateTime(long createTime);
}
