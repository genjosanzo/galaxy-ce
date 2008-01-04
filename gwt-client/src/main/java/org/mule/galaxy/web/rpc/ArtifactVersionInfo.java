package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

public class ArtifactVersionInfo implements IsSerializable {
    private String versionLabel;
    private String link;
    private Date created;
    private String authorName;
    private String authorUsername;
    private boolean active;
    
    public ArtifactVersionInfo(String versionLabel, String link, Date created, boolean active, String authorName,
                               String authorUsername) {
        super();
        this.versionLabel = versionLabel;
        this.link = link;
        this.active = active;
        this.created = created;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
    }

    public ArtifactVersionInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
