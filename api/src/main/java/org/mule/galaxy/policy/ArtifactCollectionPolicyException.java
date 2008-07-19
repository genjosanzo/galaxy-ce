package org.mule.galaxy.policy;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.EntryVersion;

public class ArtifactCollectionPolicyException extends Exception {
    private Map<EntryVersion,List<ApprovalMessage>> policyFailures;

    public ArtifactCollectionPolicyException(Map<EntryVersion, List<ApprovalMessage>> policyFailures) {
        super();
        this.policyFailures = policyFailures;
    }

    public Map<EntryVersion, List<ApprovalMessage>> getPolicyFailures() {
        return policyFailures;
    }
}
