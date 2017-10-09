package org.classfoo.onyx.impl.web.apis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.operate.OnyxOperateAddLabel;
import org.classfoo.onyx.api.operate.OnyxOperateSaveLabel;
import org.classfoo.onyx.api.operate.OnyxOperateUpdateLabel;
import org.classfoo.onyx.api.query.OnyxQueryLabel;
import org.classfoo.onyx.api.query.OnyxQueryLabels;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.OnyxUtils;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jodd.util.HtmlEncoder;

/**
 * KnowledgeBases Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_Label extends OnyxApiImpl implements OnyxApi {

	private static final String LABEL = "label";

	@Autowired
	private OnyxService onyxService;

	@Override
	public String getResource() {
		return LABEL;
	}

	@Override
	public Object getSingle(String resid, Map<String, Object> args) throws UnsupportedEncodingException {
		OnyxQueryLabel queryLabel = onyxService.createQuery(OnyxQueryLabel.class);
		String kid = MapUtils.getString(args, "kid");
		queryLabel.setKid(kid);
		String name = URLDecoder.decode(resid, "UTF-8");
		queryLabel.setName(name);
		return queryLabel.querySingle();
	}

	@Override
	public Object getList(Map<String, Object> args) {
		OnyxQueryLabels queryLabels = onyxService.createQuery(OnyxQueryLabels.class);
		String kid = MapUtils.getString(args, "kid");
		queryLabels.setKid(kid);
		return queryLabels.queryList();
	}

	@Override
	public Object post(Map<String, Object> args) {
		String kid = OnyxUtils.readJson(args, "kid", String.class);
		String lid = OnyxUtils.readJson(args, "lid", String.class);
		String name = OnyxUtils.readJson(args, "name", String.class);
		List<Map<String, Object>> modifies = OnyxUtils.readJson(args, "modifies", List.class);
		OnyxOperateSaveLabel saveLabel = onyxService.createOperate(OnyxOperateSaveLabel.class);
		saveLabel.setKid(kid);
		saveLabel.setLid(lid);
		saveLabel.setName(name);
		saveLabel.setModifies(modifies);
		return saveLabel.commit();
	}

	@Override
	public Object put(Map<String, Object> args) {
		String kid = OnyxUtils.readJson(args, "kid", String.class);
		String lid = OnyxUtils.readJson(args, "lid", String.class);
		String name = OnyxUtils.readJson(args, "name", String.class);
		List<String> properties = OnyxUtils.readJson(args, "properties", List.class);
		List<String> parents = OnyxUtils.readJson(args, "parents", List.class);
		List<String> links = OnyxUtils.readJson(args, "links", List.class);
		OnyxOperateUpdateLabel updateLabel = onyxService.createOperate(OnyxOperateUpdateLabel.class);
		updateLabel.setKnowledgeBase(kid);
		updateLabel.setLabelId(lid);
		updateLabel.setLabelName(name);
		updateLabel.setParents(parents);
		updateLabel.setLinks(links);
		updateLabel.setProperties(properties);
		return updateLabel.commit();
	}
}
