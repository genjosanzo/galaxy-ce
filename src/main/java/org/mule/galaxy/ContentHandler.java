package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ContentHandler {
    String getName(Object o);
    
    String getContentType(Object o);
    
    String describe(ArtifactVersion v);
    
    String desribeDifferences(ArtifactVersion v1, ArtifactVersion v2);
    
    Object read(InputStream stream) throws IOException;

    InputStream read(Object data) throws IOException;
    
    void write(Object o, OutputStream stream) throws IOException;

}
