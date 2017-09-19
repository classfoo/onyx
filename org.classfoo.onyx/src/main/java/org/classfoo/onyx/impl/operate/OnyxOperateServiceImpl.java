package org.classfoo.onyx.impl.operate;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperate;
import org.classfoo.onyx.api.operate.OnyxOperateAddEntity;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.operate.OnyxOperateAddMaterial;
import org.classfoo.onyx.api.operate.OnyxOperateBatch;
import org.classfoo.onyx.api.operate.OnyxOperateSaveLabel;
import org.classfoo.onyx.api.operate.OnyxOperateService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateEntity;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateLabel;
import org.springframework.stereotype.Component;

@Component
public class OnyxOperateServiceImpl implements OnyxOperateService {

	private static final Map<Class, Class> map = new HashMap<Class, Class>(100);

	static {
		map.put(OnyxOperateAddLabel.class, OnyxOperateAddLabelImpl.class);
		map.put(OnyxOperateUpdateLabel.class, OnyxOperateUpdateLabelImpl.class);
		map.put(OnyxOperateSaveLabel.class, OnyxOperateSaveLabelImpl.class);
		map.put(OnyxOperateUpdateEntity.class, OnyxOperateUpdateEntityImpl.class);
		map.put(OnyxOperateAddMaterial.class, OnyxOperateAddMaterialImpl.class);
		map.put(OnyxOperateBatch.class, OnyxOperateBatchImpl.class);
		map.put(OnyxOperateAddEntity.class, OnyxOperateAddEntityImpl.class);
	}

	@Override
	public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type) {
		Class<T> query = map.get(type);
		if (query == null) {
			throw new RuntimeException();
		}
		try {
			Constructor<T> constructor = query.getConstructor(OnyxService.class);
			return constructor.newInstance(onyxService);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
