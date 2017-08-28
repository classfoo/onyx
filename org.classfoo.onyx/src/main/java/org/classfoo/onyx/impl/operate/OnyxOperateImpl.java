package org.classfoo.onyx.impl.operate;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperate;

public abstract class OnyxOperateImpl implements OnyxOperate {

	protected OnyxService onyxService;

	public OnyxOperateImpl(OnyxService onyxService) {
		this.onyxService = onyxService;
	}
}
