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

	public void setDescription(String description);

	/**
	 * get knowledge base category
	 * @return
	 */
	public String getCategory();

	public void setCategory(String category);

	/**
	 * get knowledge base creator
	 * @return
	 */
	public String getCreator();

	public void setCreator(String creator);

	/**
	 * get knowledge base create time
	 * @return
	 */
	public long getCreateTime();

	public void setCreateTime(long createTime);
}
