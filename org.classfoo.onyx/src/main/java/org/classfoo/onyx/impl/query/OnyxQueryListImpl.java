package org.classfoo.onyx.impl.query;

import java.util.List;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.query.OnyxQueryList;

public abstract class OnyxQueryListImpl<T> extends OnyxQueryImpl implements OnyxQueryList<T> {

	public OnyxQueryListImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public List<T> queryList() {
		return this.queryList(-1);
	}
}
