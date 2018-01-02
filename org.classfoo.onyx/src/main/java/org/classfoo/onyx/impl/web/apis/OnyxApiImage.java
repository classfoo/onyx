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
import org.apache.http.HttpStatus;
import org.classfoo.onyx.api.OnyxService;
import org.classfoo.onyx.api.file.OnyxFileService;
import org.classfoo.onyx.api.query.OnyxQueryEntities;
import org.classfoo.onyx.api.query.OnyxQueryEntity;
import org.classfoo.onyx.api.web.OnyxApi;
import org.classfoo.onyx.impl.web.AbstractOnyxApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Onyx Image Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiImage extends AbstractOnyxApi implements OnyxApi {

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
        response.sendError(HttpStatus.SC_NOT_FOUND);
        return null;
    }
}
