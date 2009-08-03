package org.mule.galaxy.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.activation.MimeType;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;

public interface ContentHandler {
    
    Set<MimeType> getSupportedContentTypes();
    
    Set<Class<?>> getSupportedTypes();
    
    String getName(Object o);
    
    MimeType getContentType(Object o);
    
    <T> T read(InputStream stream, Item workspace) throws IOException;

    InputStream read(Object data) throws IOException;
    
    void write(Object o, OutputStream stream) throws IOException;

    Set<String> detectDependencies(Object o, Item w);
    
    /**
     * This will be called after the registry is initialized so the ContentHandler
     * can resolve imported artifacts.
     * @param registry
     */
    void setRegistry(Registry registry);
}
