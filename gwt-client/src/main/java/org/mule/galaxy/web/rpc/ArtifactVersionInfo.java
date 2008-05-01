package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArtifactVersionInfo implements IsSerializable {
    private String id;
    private String versionLabel;
    private String link;
    private Date created;
    private String authorName;
    private String authorUsername;
    private boolean _default;
    private boolean enabled;
    private String phase;
    private boolean indexInformationStale;
    
    /*
     * @gwt typeArgs org.mule.galaxy.rpc.WProperty
     */
    private List properties = new ArrayList();
    
    public ArtifactVersionInfo(String id, String versionLabel, String link, Date created, boolean _default,
                               boolean enabled,
                               String authorName, 
                               String authorUsername,
                               String phase,
                               boolean indexInformationStale) {
        super();
        this.id = id;
        this.versionLabel = versionLabel;
        this.link = link;
        this._default = _default;
        this.enabled = enabled;
        this.created = created;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
        this.phase = phase;
        this.indexInformationStale = indexInformationStale;
    }

    public ArtifactVersionInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean isIndexInformationStale() {
        return indexInformationStale;
    }

    public void setIndexInformationStale(boolean indexInformationStale) {
        this.indexInformationStale = indexInformationStale;
    }

    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WProperty
     */
    public List getProperties() {
        return properties;
    }
    
    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefault() {
        return _default;
    }

    public void setDefault(boolean _default) {
        this._default = _default;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getVersionLabel() {
        return versionLabel;
    }
    
    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
}
