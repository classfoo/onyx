package org.classfoo.onyx.api.operate;

import java.util.List;

public interface OnyxOperateUpdateLabel extends OnyxOperate {

	void setKnowledgeBase(String kid);

	void setLabelId(String lid);

	void setLabelName(String name);

	void setParents(List<String> parents);

	void setLinks(List<String> links);

	void setProperties(List<String> properties);

}
