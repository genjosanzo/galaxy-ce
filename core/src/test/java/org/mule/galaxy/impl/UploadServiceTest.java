package org.mule.galaxy.impl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.mule.galaxy.impl.artifact.UploadService;
import org.mule.galaxy.impl.artifact.UploadServiceImpl;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class UploadServiceTest extends AbstractGalaxyTest {
    protected UploadService uploadService;

    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-core-extensions.xml",
            "/META-INF/applicationContext-acegi-security.xml",
            "/META-INF/applicationContext-cache.xml",            
            "classpath*:/META-INF/galaxy-applicationContext.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }
    
    public void testUpload() throws Exception {
        byte[] data = new byte[] { 0, 1, 2, 3 };
        
        String id = uploadService.upload(new ByteArrayInputStream(data));
        
        InputStream stream = uploadService.getFile(id);
        assertNotNull(stream);
        
        uploadService.delete(id);
        
        try {
            uploadService.getFile(id);
            fail("File should not be found");
        } catch (FileNotFoundException e) {
        }
        
        id = uploadService.upload(new ByteArrayInputStream(data));
        ((UploadServiceImpl) uploadService).setStoragePeriod(0);
        uploadService.clean();
        

        try {
            uploadService.getFile(id);
            fail("File should not be found");
        } catch (FileNotFoundException e) {
        }
    }
}
