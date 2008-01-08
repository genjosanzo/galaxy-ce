package org.mule.galaxy;

import java.util.List;
import java.util.ResourceBundle;

import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class ArtifactPolicyException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(ArtifactPolicyException.class);
    
    private List<ApprovalMessage> approvals;

    public ArtifactPolicyException(List<ApprovalMessage> approvals) {
        super(new Message("ARTIFACT_NOT_APPROVED", BUNDLE));
        this.approvals = approvals;
    }

    public List<ApprovalMessage> getApprovals() {
        return approvals;
    }
    
}
