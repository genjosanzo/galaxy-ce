package org.mule.galaxy;

import java.util.Collection;

import org.mule.galaxy.policy.Approval;

public class ArtifactResult {
    private Artifact artifact;
    private ArtifactVersion artifactVersion;
    private Collection<Approval> approvals;
    private boolean approved = true;
    
    public ArtifactResult(Artifact artifact, 
                          ArtifactVersion artifactVersion, 
                          Collection<Approval> approvals) {
        super();
        this.artifact = artifact;
        this.artifactVersion = artifactVersion;
        this.approvals = approvals;
        
        for (Approval a : approvals) {
            if (!a.isApproved()) {
                approved = false;
                break;
            }
        }
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public ArtifactVersion getArtifactVersion() {
        return artifactVersion;
    }

    public Collection<Approval> getApprovals() {
        return approvals;
    }

    public boolean isApproved() {
        return approved;
    }
    
}
