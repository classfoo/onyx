package org.classfoo.onyx.impl.operate;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateSaveLabel;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;
import org.classfoo.onyx.impl.OnyxUtils;

public class OnyxOperateSaveLabelImpl extends OnyxOperateImpl implements OnyxOperateSaveLabel {

	private String labelName;

	private String lid;

	private String kid;

	private List<Map<String, Object>> modifies;

	public OnyxOperateSaveLabelImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setKid(String kid) {
		this.kid = kid;
	}

	@Override
	public void setLid(String lid) {
		this.lid = lid;
	}

	@Override
	public void setName(String name) {
		this.labelName = name;
	}

	@Override
	public void setModifies(List<Map<String, Object>> modifies) {
		this.modifies = modifies;
	}

	@Override
	public Map<String, Object> commit() {
		if (StringUtils.isBlank(lid)) {
			return this.commit_create();
		}
		else {
			return this.commit_save();
		}
	}

	private Map<String, Object> commit_save() {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		return storage.saveLabelModifies(this.kid, this.lid, this.labelName, modifies);
	}

	private Map<String, Object> commit_create() {
		OnyxStorageService storageService = this.onyxService.getStorageService();
		OnyxStorage storage = storageService.getStorage();
		String lid = OnyxUtils.getRandomUUID("l");
		return storage.saveLabelModifies(this.kid, lid, this.labelName, modifies);
	}

}
