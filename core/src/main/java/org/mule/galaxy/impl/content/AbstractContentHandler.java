package org.mule.galaxy.impl.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;

public abstract class AbstractContentHandler implements ContentHandler {

    public static final String DESCRIPTION = "description";
    
    protected Set<MimeType> supportedContentTypes = new HashSet<MimeType>();
    protected Set<Class<?>> supportedTypes = new HashSet<Class<?>>();
    protected Set<String> supportedFileExtensions = new HashSet<String>();

    protected Registry registry;
    
    public Set<Class<?>> getSupportedTypes() {
        return supportedTypes;
    }

    public Set<MimeType> getSupportedContentTypes() {
        return supportedContentTypes;
    }
    
    public Set<String> getSupportedFileExtensions() {
        return supportedFileExtensions;
    }

    public void addMetadata(ArtifactVersion v) {

    }

    public String describe(ArtifactVersion v) {
        ArtifactVersion prev = (ArtifactVersion) v.getPrevious();
        
        if (prev == null)
            return "Initial version.";
        
        
        return describeDifferences(prev, v);
    }

    public String describeDifferences(ArtifactVersion prev, ArtifactVersion v) {
        return "Version " + v.getVersionLabel();
    }

    public InputStream read(Object data) throws IOException {
        final File temp = File.createTempFile("galaxyOut", "tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(temp);
        try {
            write(data, fileOutputStream);
        } finally {
            fileOutputStream.close();
        }
        
        return new FileInputStream(temp) {
            @Override
            public void close() throws IOException {
                super.close();
                
                temp.delete();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Set<String> detectDependencies(Object o, Workspace w) {
        return Collections.EMPTY_SET;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

}
