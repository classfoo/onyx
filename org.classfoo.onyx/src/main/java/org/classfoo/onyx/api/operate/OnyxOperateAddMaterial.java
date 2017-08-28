package org.classfoo.onyx.api.operate;

import java.util.Map;

public interface OnyxOperateAddMaterial extends OnyxOperate {

	void setKid(String kid);

	void setName(String name);

	void setDesc(String desc);

	void setProperties(Map<String, Object> properties);

}
