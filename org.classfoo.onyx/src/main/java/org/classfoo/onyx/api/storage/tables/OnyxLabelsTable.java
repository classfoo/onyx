package org.classfoo.onyx.api.storage.tables;

public interface OnyxLabelsTable {

	public String getUUID();

	public void setUUID(String uuid);

	public String getName();

	public void setName(String name);

	public String getKnowledgeBase();

	public void setKnowledgeBase(String knowledgeBase);
}
