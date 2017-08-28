package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;

public class OnyxOperateAddLabelImpl extends OnyxOperateImpl implements OnyxOperateAddLabel {

	private String labelName;

	private List<String> parents;

	private List<String> links;

	private List<String> properties;

	private String kid;

	public OnyxOperateAddLabelImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKnowledgeBase(String kid) {
		this.kid = kid;
	}

	@Override
	public void setLabelName(String name) {
		this.labelName = name;
	}

	@Override
	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	@Override
	public void setLinks(List<String> links) {
		this.links = links;
	}

	@Override
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	@Override
	public Map<String, Object> commit() {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		return storage.createLabel(this.kid, this.labelName, this.parents, this.links, this.properties);
	}

}
