package org.mule.galaxy.web.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DependencyInfo implements IsSerializable {
    private boolean userSpecified;
    private boolean dependsOn;
    private String artifactName;
    private String artifactId;
    
    public DependencyInfo() {
        super();
    }
    public DependencyInfo(boolean userSpecified, boolean dependsOn, String artifactName, String artifactId) {
        super();
        this.userSpecified = userSpecified;
        this.dependsOn = dependsOn;
        this.artifactName = artifactName;
        this.artifactId = artifactId;
    }
    
    public boolean isUserSpecified() {
        return userSpecified;
    }
    public void setUserSpecified(boolean userSpecified) {
        this.userSpecified = userSpecified;
    }
    public boolean isDependsOn() {
        return dependsOn;
    }
    public void setDependsOn(boolean dependsOn) {
        this.dependsOn = dependsOn;
    }
    public String getArtifactName() {
        return artifactName;
    }
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    
}
