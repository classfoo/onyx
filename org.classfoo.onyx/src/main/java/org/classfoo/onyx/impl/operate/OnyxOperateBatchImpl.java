package org.classfoo.onyx.impl.operate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperate;
import org.classfoo.onyx.api.operate.OnyxOperateBatch;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * @see OnyxOperateBatch
 * @author ClassFoo
 *
 */
public class OnyxOperateBatchImpl extends OnyxOperateImpl implements OnyxOperateBatch {

	private ArrayList<OnyxOperate> operates = new ArrayList<OnyxOperate>(10);

	public OnyxOperateBatchImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public OnyxOperateBatch addOperate(OnyxOperate operate) {
		operates.add(operate);
		return this;
	}

	@Override
	public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type) {
		T operate = this.onyxService.createOperate(type);
		this.addOperate(operate);
		return operate;
	}

	@Override
	public Map<String, Object> execute(OnyxStorageSession session) {
		Map<String, Object> result = new HashMap<String, Object>(1);
		ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>(this.operates.size());
		for (OnyxOperate operate : this.operates) {
			Map<String, Object> map = ((OnyxOperateImpl) operate).execute(session);
			results.add(map);
		}
		result.put("results", results);
		return result;
	}

}
