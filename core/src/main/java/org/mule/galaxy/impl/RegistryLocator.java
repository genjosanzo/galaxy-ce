package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.xml.WSDLLocator;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.LogUtils;

import org.xml.sax.InputSource;

public class RegistryLocator implements WSDLLocator {

    private Logger LOGGER = LogUtils.getL7dLogger(RegistryLocator.class);

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
            LOGGER.log(Level.WARNING, "Could not close wsdl stream.", e);
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
        if (importLoc.indexOf("://") == -1) {
            String[] paths = importLoc.split("/");
            
            for (int i = 0; i < paths.length - 1; i++) {
                String p = paths[i];
                
                // TODO: escaping?
                if (p.equals("..")) {
                    w = w.getParent();
                } else if (!p.equals(".")) {
                    w = w.getWorkspace(p);
                }
            }

            try {
                Artifact artifact = registry.getArtifact(w, paths[paths.length-1]);
                
                InputStream is = artifact.getLatestVersion().getStream();
                InputSource source = new InputSource(is);
                source.setSystemId(artifact.getPath());
                return source;
            } catch (NotFoundException e) {
            }
        }
        
        try {
            URL url = new URL(importLoc);
            InputSource source = new InputSource(url.openStream());
            source.setSystemId(importLoc);
            return source;
        } catch (IOException e) {
            return new InputSource(importLoc);
        }
    }

    public String getLatestImportURI() {
        return latest;
    }

}
