package org.classfoo.onyx.api.operate;

import java.util.Map;

/**
 * Onyx Operate Add Entity
 * @author ClassFoo
 *
 */
public interface OnyxOperateAddEntity extends OnyxOperate {

	void setKnowledgeBase(String kid);

	void setName(String name);

	void setProperties(Map<String, Object> properties);

}
