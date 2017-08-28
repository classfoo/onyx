package org.classfoo.onyx.impl.web.apis;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddMaterial;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryMaterial;
import org.classfoo.onyx.api.query.OnyxQueryMaterials;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * KnowledgeBases Material Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_Material extends OnyxApiImpl implements OnyxApi {

	private static final String MATERIAL = "material";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return MATERIAL;
	}

	@Override
	public Object getSingle(String resid, Map<String, Object> args) {
		OnyxQueryMaterial queryMaterial = this.onyxService.createQuery(OnyxQueryMaterial.class);
		queryMaterial.setMid(resid);
		return queryMaterial.querySingle();
	}

	@Override
	public Object getList(Map<String, Object> args) {
		String kid = MapUtils.getString(args, "kid");
		OnyxQueryMaterials queryMaterials = this.onyxService.createQuery(OnyxQueryMaterials.class);
		queryMaterials.setKid(kid);
		return queryMaterials.queryList();
	}

	@Override
	public Object post(Map<String, Object> args) {
		OnyxOperateAddMaterial addMaterial = onyxService.createOperate(OnyxOperateAddMaterial.class);
		String kid = MapUtils.getString(args, "kid");
		addMaterial.setKid(kid);
		String name = MapUtils.getString(args, "name");
		addMaterial.setName(name);
		String desc = MapUtils.getString(args, "desc");
		addMaterial.setDesc(desc);
		return addMaterial.commit();
	}
}
