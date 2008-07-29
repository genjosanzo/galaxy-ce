package org.mule.galaxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.policy.ApprovalMessage;

public class EntryResult {
    private Entry artifact;
    private EntryVersion artifactVersion;
    private Map<Item, List<ApprovalMessage>> approvals;
    private boolean approved = true;
    
    public EntryResult(Entry artifact, 
                       EntryVersion artifactVersion, 
                       Map<Item, List<ApprovalMessage>> approvals) {
        super();
        this.artifact = artifact;
        this.artifactVersion = artifactVersion;
        this.approvals = approvals;
    }

    public Entry getEntry() {
        return artifact;
    }

    public EntryVersion getEntryVersion() {
        return artifactVersion;
    }


    public Map<Item, List<ApprovalMessage>> getApprovals() {
        return approvals;
    }

    public boolean isApproved() {
        return approved;
    }
    
}
