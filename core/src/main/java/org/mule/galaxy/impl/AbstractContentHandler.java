package org.mule.galaxy.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;
import javax.xml.transform.Source;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;

public abstract class AbstractContentHandler implements ContentHandler {

    public static final String DESCRIPTION = "description";
    
    protected Set<MimeType> supportedContentTypes = new HashSet<MimeType>();
    protected Set<Class<?>> supportedTypes = new HashSet<Class<?>>();

    public Set<Class<?>> getSupportedTypes() {
        return supportedTypes;
    }

    public Set<MimeType> getSupportedContentTypes() {
        return supportedContentTypes;
    }
    
    public Object read(Source source) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void addMetadata(ArtifactVersion v) {
        v.setProperty(DESCRIPTION, describe(v));
    }

    public String describe(ArtifactVersion v) {
        ArtifactVersion prev = v.getPrevious();
        
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

}
