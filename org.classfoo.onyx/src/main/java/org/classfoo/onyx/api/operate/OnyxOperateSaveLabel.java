package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

public interface OnyxOperateSaveLabel extends OnyxOperate {

	void setKid(String kid);

	void setLid(String lid);

	void setName(String name);

	void setModifies(List<Map<String, Object>> modifies);

}
