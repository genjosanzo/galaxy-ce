package org.mule.galaxy.api;

public interface Dependency {
    
    Artifact getArtifact();
    
    /**
     * Was this dependency detected by Galaxy or was it specified by the user.
     * @return
     */
    boolean isUserSpecified();
    
}
