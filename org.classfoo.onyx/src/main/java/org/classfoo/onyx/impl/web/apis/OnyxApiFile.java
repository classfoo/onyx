package org.classfoo.onyx.impl.web.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
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
 * KnowledgeBases Entity Api
 * @author ClassFoo
 *
 */
@Component
public class OnyxApiFile extends AbstractOnyxApi implements OnyxApi {

    private static final String FILE = "file";

    @Autowired
    private OnyxService onyxService;

    @Autowired
    private OnyxFileService fileService;

    @Override
    public String getResource() {
        return FILE;
    }

    @Override
    public Object getSingle(String resid, Map<String, Object> args) {
        OnyxQueryEntity queryEntity = this.onyxService.createQuery(OnyxQueryEntity.class);
        queryEntity.setEid(resid);
        return queryEntity.querySingle();
    }

    @Override
    public Object getList(Map<String, Object> args) {
        String kid = MapUtils.getString(args, "kid");
        OnyxQueryEntities queryEntities = this.onyxService.createQuery(OnyxQueryEntities.class);
        queryEntities.setKid(kid);
        return queryEntities.queryList();
    }

    @Override
    public Object post(Map<String, Object> args) {
        MultipartFile[] files = (MultipartFile[]) args.get("file");
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>(files.length);
        for (MultipartFile file : files) {
            String id = this.fileService.saveAsTempFile(file);
            HashMap<String, Object> item = new HashMap<String, Object>(1);
            item.put("id", id);
            item.put("name", file.getOriginalFilename());
            result.add(item);
        }
        return result;
    }

}
