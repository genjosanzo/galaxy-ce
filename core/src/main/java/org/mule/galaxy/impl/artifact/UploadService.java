package org.mule.galaxy.impl.artifact;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Provides temporary storage for artifacts which are uploaded.
 */
public interface UploadService {
    String upload(InputStream inputStream);
    
    InputStream getFile(String name) throws FileNotFoundException;
    
    void clean();
    
    void delete(String name);
}
