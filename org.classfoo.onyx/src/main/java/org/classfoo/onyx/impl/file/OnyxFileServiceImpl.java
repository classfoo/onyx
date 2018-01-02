package org.classfoo.onyx.impl.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.classfoo.onyx.api.file.OnyxFileService;
import org.classfoo.onyx.impl.OnyxUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @see OnyxFileService
 * @author ClassFoo
 *
 */
@Component
public class OnyxFileServiceImpl implements OnyxFileService {

    @Override
    public String createTempFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File getTempFile(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String saveAsTempFile(MultipartFile file) {
        File tempdir = FileUtils.getTempDirectory();
        String id = OnyxUtils.getRandomUUID("f");
        File dir = new File(tempdir, id);
        dir.mkdir();
        File tempFile = new File(dir, file.getOriginalFilename());
        try {
            InputStream fis = file.getInputStream();
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                try {
                    IOUtils.copy(fis, bos);
                }
                finally {
                    IOUtils.closeQuietly(bos);
                }
            }
            finally {
                IOUtils.closeQuietly(fis);
            }
            return id;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
