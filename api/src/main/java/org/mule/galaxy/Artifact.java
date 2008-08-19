package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;

/**
 * An artifact which can be versioned inside a repository. Can be anything
 * from a configuration file to a wsdl to a jar etc.
 */
public interface Artifact extends Entry {

    ContentHandler getContentHandler();
    
    MimeType getContentType();
    
    QName getDocumentType();
    
    EntryResult newVersion(InputStream inputStream, String versionLabel) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;

    EntryResult newVersion(Object data, String versionLabel) 
        throws RegistryException, PolicyException, IOException, DuplicateItemException, AccessException;

}
