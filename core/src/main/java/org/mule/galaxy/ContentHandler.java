package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.activation.MimeType;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

public interface ContentHandler {
    
    Set<MimeType> getSupportedContentTypes();
    
    Set<Class<?>> getSupportedTypes();
    
    String getName(Object o);
    
    MimeType getContentType(Object o);
    
    void addMetadata(ArtifactVersion v);
    
    Object read(InputStream stream) throws IOException;

    InputStream read(Object data) throws IOException;
    
    void write(Object o, OutputStream stream) throws IOException;

}
