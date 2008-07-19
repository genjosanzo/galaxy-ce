package org.mule.galaxy;

import java.util.Collection;

import org.mule.galaxy.policy.ApprovalMessage;

public class EntryResult {
    private Entry artifact;
    private EntryVersion artifactVersion;
    private Collection<ApprovalMessage> approvals;
    private boolean approved = true;
    
    public EntryResult(Entry artifact, 
                       EntryVersion artifactVersion, 
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

    public Entry getEntry() {
        return artifact;
    }

    public EntryVersion getEntryVersion() {
        return artifactVersion;
    }

    public Collection<ApprovalMessage> getApprovals() {
        return approvals;
    }

    public boolean isApproved() {
        return approved;
    }
    
}
