package org.classfoo.onyx.impl.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.cache.OnyxCacheQuery;
import org.classfoo.onyx.api.cache.OnyxCacheService;
import org.classfoo.onyx.api.query.OnyxQueryLabel;
import org.classfoo.onyx.api.storage.OnyxStorage;
import org.classfoo.onyx.api.storage.OnyxStorageService;

public class OnyxQueryLabelImpl extends OnyxQuerySingleImpl<Map<String, Object>> implements OnyxQueryLabel {

	private String lid;

	public OnyxQueryLabelImpl(OnyxService onyxService) {
		super(onyxService);
	}

	@Override
	public void setLid(String lid) {
		this.lid = lid;
	}

	@Override
	public int hashCode() {
		return this.lid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OnyxQueryLabelImpl)) {
			return false;
		}
		OnyxQueryLabelImpl queryLabel = (OnyxQueryLabelImpl) obj;
		if (!StringUtils.equals(this.lid, queryLabel.lid)) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> querySingle() {
		OnyxCacheService cacheService = this.onyxService.getCacheService();
		Map<String, Object> result = cacheService.query(this, new OnyxCacheQuery<Map<String, Object>>() {
			@Override
			public Map<String, Object> query() {
				OnyxStorageService storageService = onyxService.getStorageService();
				OnyxStorage storage = storageService.getStorage();
				List<Map<String, Object>> modifies = storage.queryLabelModifies(lid);
				return convertToLabel(modifies);
			}

		});
		return result;
	}

	private Map<String, Object> convertToLabel(List<Map<String, Object>> modifies) {
		HashMap<String, Object> label = new HashMap<String, Object>(10);
		for (Map<String, Object> modify : modifies) {
			this.modifyLabel(label, modify);
		}
		return label;
	}

	private void modifyLabel(Map<String, Object> label, Map<String, Object> modify) {
		String lid = MapUtils.getString(modify, "lid");
		String kid = MapUtils.getString(modify, "kid");
		label.put("lid", lid);
		label.put("kid", kid);
		String key = MapUtils.getString(modify, "key");
		if ("name".equals(key)) {
			String name = MapUtils.getString(modify, "name");
			label.put("name", name);
		}
		else if ("parents".equals(key)) {
			String operate = MapUtils.getString(modify, "operate");
			if ("create".equals(operate)) {
				String parent = MapUtils.getString(modify, "parent");
				List<Map<String, Object>> parents = (List<Map<String, Object>>) label.get("parents");
				if (parents == null) {
					parents = new ArrayList<Map<String, Object>>(10);
					label.put("parents", parents);
				}
				HashMap<String, Object> item = new HashMap<String, Object>(3);
				item.put("name", parent);
				item.put("caption", parent);
				parents.add(item);
			}
			else if ("delete".equals(operate)) {

			}
		}
		else if ("properties".equals(key)) {
			String operate = MapUtils.getString(modify, "operate");
			if ("create".equals(operate)) {
				String pname = MapUtils.getString(modify, "pname");
				String ptype = MapUtils.getString(modify, "ptype");
				List<Map<String, Object>> properties = (List<Map<String, Object>>) label.get("properties");
				if (properties == null) {
					properties = new ArrayList<Map<String, Object>>(10);
					label.put("properties", properties);
				}
				HashMap<String, Object> property = new HashMap<String, Object>(3);
				property.put("name", pname);
				property.put("type", ptype);
				properties.add(property);
			}
			else if ("update".equals(operate)) {
				String pname = MapUtils.getString(modify, "pname");
				String ptype = MapUtils.getString(modify, "ptype");
				List<Map<String, Object>> properties = (List<Map<String, Object>>) label.get("properties");
				for (Map<String, Object> property : properties) {
					if (StringUtils.equals(pname, MapUtils.getString(property, "name"))) {
						property.put("type", ptype);
					}
				}
			}
			else if ("delete".equals(operate)) {

			}
		}
		else {

		}
	}
}
