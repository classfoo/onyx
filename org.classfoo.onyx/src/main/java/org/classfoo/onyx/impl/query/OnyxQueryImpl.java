package org.classfoo.onyx.impl.query;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQuery;

public class OnyxQueryImpl implements OnyxQuery {

	protected OnyxService onyxService;

	public OnyxQueryImpl(OnyxService onyxService) {
		this.onyxService = onyxService;
	}
}
