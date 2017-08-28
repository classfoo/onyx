package org.classfoo.onyx.api.operate;

import org.classfoo.onyx.api.OnyxService;

public interface OnyxOperateService {
	public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type);
}
