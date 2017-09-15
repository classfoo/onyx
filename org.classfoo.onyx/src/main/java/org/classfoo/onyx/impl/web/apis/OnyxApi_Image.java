package org.classfoo.onyx.impl.web.apis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.file.OnyxFileService;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.OnyxApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Onyx Image Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApi_Image extends OnyxApiImpl implements OnyxApi {

	private static final String IMAGE = "image";

	@Autowired
	private OnyxService onyxService;

	@Autowired
	private OnyxFileService fileService;

	@Override
	public String getResource() {
		return IMAGE;
	}

	@Override
	public Object get(Map<String, Object> args) throws IOException {
		HttpServletResponse response = (HttpServletResponse) MapUtils.getObject(args, "response");
		ServletOutputStream os = response.getOutputStream();
		InputStream is = OnyxApi_Image.class.getResourceAsStream("image.png");
		try {
			IOUtils.copy(is, os);
		}
		finally {
			is.close();
		}
		return null;
	}
}
