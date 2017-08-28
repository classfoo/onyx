package org.classfoo.onyx.api.query;

import java.util.Map;

public interface OnyxQueryEntity extends OnyxQuerySingle<Map<String, Object>> {

	/**
	 * set entity id
	 * @param eid
	 */
	public void setEid(String eid);
}
