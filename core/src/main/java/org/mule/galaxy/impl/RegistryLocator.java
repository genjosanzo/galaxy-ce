package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.type.TypeManager;
import org.xml.sax.InputSource;

public class RegistryLocator implements WSDLLocator {

    private final Log log = LogFactory.getLog(getClass());

    private Registry registry;
    
    private InputSource src;
    
    private InputStream inputStream;

    private Item workspace;

    private String latest;

    public RegistryLocator(InputStream is, Registry registry, Item w) {
        this(registry, w);
        this.src = new InputSource(is);
        this.inputStream = is;
    }
    
    public RegistryLocator(Registry registry, Item w) {
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
        
        Item w = workspace;
        try {
            if (importLoc.indexOf("://") == -1) {
                Item item = registry.resolve(w, importLoc);
                if (item != null) {
                    Item version = item.getProperty(TypeManager.DEFAULT_VERSION);
                    if (version == null) {
                        version = item.getLatestItem();
                    }
                    Artifact a = version.getProperty("artifact");
                    if (a != null) {
                        InputStream is = a.getInputStream();
                        InputSource source = new InputSource(is);
                        source.setSystemId(item.getPath());
                        return source;
                    }
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
