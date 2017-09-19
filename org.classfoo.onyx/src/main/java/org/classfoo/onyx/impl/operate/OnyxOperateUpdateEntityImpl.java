package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateEntity;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

public class OnyxOperateUpdateEntityImpl extends OnyxOperateImpl implements OnyxOperateUpdateEntity {

	private String kid;

	private String eid;

	private List<Map<String, Object>> modifies;

	public OnyxOperateUpdateEntityImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKnowledgeBase(String kid) {
		this.kid = kid;
	}

	@Override
	public void setEntityId(String eid) {
		this.eid = eid;

	}

	@Override
	public void setModifies(List<Map<String, Object>> modifies) {
		this.modifies = modifies;
	}

	@Override
	public Map<String, Object> execute(OnyxStorageSession session) {
		return session.updateEntity(this.kid, this.eid, this.modifies);
	}

}
