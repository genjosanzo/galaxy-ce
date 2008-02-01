package org.mule.galaxy;

import java.util.Collection;

import org.mule.galaxy.policy.ApprovalMessage;

public class ArtifactResult {
    private Artifact artifact;
    private ArtifactVersion artifactVersion;
    private Collection<ApprovalMessage> approvals;
    private boolean approved = true;
    
    public ArtifactResult(Artifact artifact, 
                          ArtifactVersion artifactVersion, 
                          Collection<ApprovalMessage> approvals) {
        super();
        this.artifact = artifact;
        this.artifactVersion = artifactVersion;
        this.approvals = approvals;
        
        for (ApprovalMessage a : approvals) {
            if (!a.isWarning()) {
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

    public Collection<ApprovalMessage> getApprovals() {
        return approvals;
    }

    public boolean isApproved() {
        return approved;
    }
    
}
