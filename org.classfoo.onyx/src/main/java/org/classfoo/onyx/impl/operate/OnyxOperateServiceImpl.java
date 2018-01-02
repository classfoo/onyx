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

/**
 * @see OnyxOperateService
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>succez</p>
 * @author ClassFoo
 * @createdate 2018年1月2日
 */
@Component
public class OnyxOperateServiceImpl implements OnyxOperateService {

    private static final Map<Class, Class> MAP = new HashMap<Class, Class>(100);

    static {
        MAP.put(OnyxOperateAddLabel.class, OnyxOperateAddLabelImpl.class);
        MAP.put(OnyxOperateUpdateLabel.class, OnyxOperateUpdateLabelImpl.class);
        MAP.put(OnyxOperateSaveLabel.class, OnyxOperateSaveLabelImpl.class);
        MAP.put(OnyxOperateUpdateEntity.class, OnyxOperateUpdateEntityImpl.class);
        MAP.put(OnyxOperateAddMaterial.class, OnyxOperateAddMaterialImpl.class);
        MAP.put(OnyxOperateBatch.class, OnyxOperateBatchImpl.class);
        MAP.put(OnyxOperateAddEntity.class, OnyxOperateAddEntityImpl.class);
    }

    @Override
    public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type) {
        Class<T> query = MAP.get(type);
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
