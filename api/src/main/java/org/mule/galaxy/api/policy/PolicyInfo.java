package org.mule.galaxy.api.policy;

public class PolicyInfo {
    private ArtifactPolicy artifactPolicy;
    private Object appliesTo;
    
    
    public PolicyInfo(ArtifactPolicy artifactPolicy, Object appliesTo) {
        super();
        this.artifactPolicy = artifactPolicy;
        this.appliesTo = appliesTo;
    }
    
    public ArtifactPolicy getArtifactPolicy() {
        return artifactPolicy;
    }
    public Object getAppliesTo() {
        return appliesTo;
    }
    
    
}
