package org.mule.galaxy;

public interface Dependency {
    
    Artifact getArtifact();
    
    boolean isUserSpecified();
    
}
