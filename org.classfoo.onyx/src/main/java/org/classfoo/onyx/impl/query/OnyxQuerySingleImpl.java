package org.classfoo.onyx.impl.query;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQuerySingle;

public abstract class OnyxQuerySingleImpl<T> extends OnyxQueryImpl implements OnyxQuerySingle<T> {

	public OnyxQuerySingleImpl(OnyxService onyxService) {
		super(onyxService);
	}

}
