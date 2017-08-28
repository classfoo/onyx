package org.classfoo.onyx.api.query;

import java.util.Map;

public interface OnyxQueryLabel extends OnyxQuerySingle<Map<String, Object>> {

	/**
	 * set label id
	 * @param eid
	 */
	public void setLid(String lid);

}
