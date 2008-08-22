package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;

import org.xml.sax.InputSource;

public class RegistryLocator implements WSDLLocator {

    private final Log log = LogFactory.getLog(getClass());

    private Registry registry;
    
    private InputSource src;
    
    private InputStream inputStream;

    private Workspace workspace;

    private String latest;

    public RegistryLocator(InputStream is, Registry registry, Workspace w) {
        this(registry, w);
        this.src = new InputSource(is);
        this.inputStream = is;
    }
    
    public RegistryLocator(Registry registry, Workspace w) {
        this.registry = registry;
        this.workspace = w;
    }
    
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            log.warn("Could not close wsdl stream.", e);
        }
    }

    public InputSource getBaseInputSource() {
        return src;
    }

    public String getBaseURI() {
        return workspace.getPath();
    }

    public InputSource getImportInputSource(String parentLocation, 
                                            String importLoc) {
        this.latest = importLoc;
        System.out.println("importing " + importLoc);
        
        Workspace w = workspace;
        try {
            if (importLoc.indexOf("://") == -1) {
                Item artifact = registry.resolve(w, importLoc);
                if (artifact != null) {
                    InputStream is = ((ArtifactVersion)((Artifact)artifact).getDefaultOrLastVersion()).getStream();
                    InputSource source = new InputSource(is);
                    source.setSystemId(artifact.getPath());
                    return source;
                }
            }
            
            
            URL url = new URL(importLoc);
            InputSource source = new InputSource(url.openStream());
            source.setSystemId(importLoc);
            return source;
        } catch (IOException e) {
            return new InputSource(importLoc);
        } catch (RegistryException e) {
            log.error(e);
            return new InputSource(importLoc);
        }
    }

    public String getLatestImportURI() {
        return latest;
    }

}
