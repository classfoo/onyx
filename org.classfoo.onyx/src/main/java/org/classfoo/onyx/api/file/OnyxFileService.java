package org.classfoo.onyx.api.file;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * File Service
 * @author ClassFoo
 *
 */
public interface OnyxFileService {

	public String createTempFile();

	public File getTempFile(String id);

	public String saveAsTempFile(MultipartFile file);
}
