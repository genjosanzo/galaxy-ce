package org.mule.galaxy.policy;

import java.util.List;
import java.util.Map;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;

public class ArtifactCollectionPolicyException extends Exception {
    private Map<ArtifactVersion,List<ApprovalMessage>> policyFailures;

    public ArtifactCollectionPolicyException(Map<ArtifactVersion, List<ApprovalMessage>> policyFailures) {
        super();
        this.policyFailures = policyFailures;
    }

    public Map<ArtifactVersion, List<ApprovalMessage>> getPolicyFailures() {
        return policyFailures;
    }
}
