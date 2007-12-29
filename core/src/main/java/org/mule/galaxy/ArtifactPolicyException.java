package org.mule.galaxy;

import java.util.Collection;
import java.util.ResourceBundle;

import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class ArtifactPolicyException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(ArtifactPolicyException.class);
    
    private Collection<ApprovalMessage> approvals;

    public ArtifactPolicyException(Collection<ApprovalMessage> approvals) {
        super(new Message("ARTIFACT_NOT_APPROVED", BUNDLE));
        this.approvals = approvals;
    }

    public Collection<ApprovalMessage> getApprovals() {
        return approvals;
    }
    
}
