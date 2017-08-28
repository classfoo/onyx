package org.classfoo.onyx.api.query;

import java.util.Map;

public interface OnyxQueryKnowledgeBase extends OnyxQuerySingle<Map<String, Object>> {

	public void setKid(String kid);
}
