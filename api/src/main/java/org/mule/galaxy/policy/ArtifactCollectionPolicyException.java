package org.mule.galaxy.policy;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.Artifact;

public class ArtifactCollectionPolicyException extends Exception {
    private Map<Artifact,List<ApprovalMessage>> policyFailures;

    public ArtifactCollectionPolicyException(Map<Artifact, List<ApprovalMessage>> policyFailures) {
        super();
        this.policyFailures = policyFailures;
    }

    public Map<Artifact, List<ApprovalMessage>> getPolicyFailures() {
        return policyFailures;
    }
}
