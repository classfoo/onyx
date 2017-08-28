package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

public interface OnyxOperateUpdateEntity extends OnyxOperate {

	void setKnowledgeBase(String kid);

	void setEntityId(String eid);

	void setModifies(List<Map<String, Object>> modifies);
}
