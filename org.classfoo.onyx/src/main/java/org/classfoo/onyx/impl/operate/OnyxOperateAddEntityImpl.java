package org.classfoo.onyx.impl.operate;

import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddEntity;
import org.classfoo.onyx.api.storage.OnyxStorageSession;

public class OnyxOperateAddEntityImpl extends OnyxOperateImpl implements OnyxOperateAddEntity {

	private String name;

	private Map<String, Object> properties;

	private String kid;

	public OnyxOperateAddEntityImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKnowledgeBase(String kid) {
		this.kid = kid;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public Map<String, Object> execute(OnyxStorageSession session) {
		return session.addEntity(this.kid, this.name, this.properties);
	}

}
