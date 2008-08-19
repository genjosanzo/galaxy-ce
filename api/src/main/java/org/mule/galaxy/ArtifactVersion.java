package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;

import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

import org.w3c.dom.Document;


public interface ArtifactVersion extends EntryVersion {
    
    /**
     * Get a Java API friendly representation of this document. This may be something
     * like a {@link Document} or a {@link Definition}.
     * @return
     */
    Object getData();
    
    InputStream getStream();
    
    Artifact getParent();
}
