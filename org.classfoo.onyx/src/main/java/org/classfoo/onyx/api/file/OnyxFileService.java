package org.classfoo.onyx.api.file;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * File Service
 * @author ClassFoo
 *
 */
public interface OnyxFileService {

    /**
     * create new temp file
     * @return
     */
    public String createTempFile();

    /**
     * get new temp file by id
     * @param id
     * @return
     */
    public File getTempFile(String id);

    /**
     * save multipart file as temp file
     * @param file
     * @return
     */
    public String saveAsTempFile(MultipartFile file);
}
