package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public interface Entry extends Item {
    
    String getPath();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String description);
    
    List<? extends EntryVersion> getVersions();

    EntryVersion getVersion(String versionName);

    /**
     * Get the default version of this entry. If this hasn't been specifically set,
     * its the latest version of the entry.
     */
    EntryVersion getDefaultOrLastVersion();

}
