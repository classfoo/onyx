package org.classfoo.onyx.api.operate;

import java.util.List;
import java.util.Map;

/**
 * Onyx Operate Add Entity
 * @author ClassFoo
 *
 */
public interface OnyxOperateAddEntity extends OnyxOperate {

	void setKnowledgeBase(String kid);

	void setName(String name);

	void setLabels(List<String> labels);

	void setProperties(Map<String, Object> properties);

}
