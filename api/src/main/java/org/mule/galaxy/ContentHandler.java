package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.activation.MimeType;

public interface ContentHandler {
    
    Set<MimeType> getSupportedContentTypes();
    
    Set<Class<?>> getSupportedTypes();
    
    String getName(Object o);
    
    MimeType getContentType(Object o);
    
    void addMetadata(ArtifactVersion v);
    
    Object read(InputStream stream, Workspace workspace) throws IOException;

    InputStream read(Object data) throws IOException;
    
    void write(Object o, OutputStream stream) throws IOException;

    Set<Item<?>> detectDependencies(Object o, Workspace w);
    
    /**
     * This will be called after the registry is initialized so the ContentHandler
     * can resolve imported artifacts.
     * @param registry
     */
    void setRegistry(Registry registry);
}
