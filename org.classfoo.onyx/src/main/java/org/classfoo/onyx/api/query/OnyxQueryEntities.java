package org.classfoo.onyx.api.query;

import java.util.Map;

public interface OnyxQueryEntities extends OnyxQueryList<Map<String, Object>> {

	/**
	 * set knowledge base id
	 * @param kid
	 */
	public void setKid(String kid);
}
