package org.classfoo.onyx.api.query;

import java.util.Map;

public interface OnyxQueryMaterial extends OnyxQuerySingle<Map<String, Object>> {

	/**
	 * set material id
	 * @param mid
	 */
	public void setMid(String mid);

}
