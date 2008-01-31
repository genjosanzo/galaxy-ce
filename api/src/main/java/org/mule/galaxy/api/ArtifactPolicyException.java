package org.mule.galaxy.api;

import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.api.util.BundleUtils;
import org.mule.galaxy.api.util.Message;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ArtifactPolicyException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(ArtifactPolicyException.class);
    
    private List<ApprovalMessage> approvals = Collections.emptyList();

    public ArtifactPolicyException(List<ApprovalMessage> approvals) {
        super(new Message("ARTIFACT_NOT_APPROVED", BUNDLE));
        this.approvals = approvals;
    }

    public List<ApprovalMessage> getApprovals() {
        return approvals;
    }

    @Override
    public String toString()
    {
        StringBuilder messages = new StringBuilder(approvals.size() * 100);
        for (ApprovalMessage approval : approvals)
        {
            messages.append(approval.toString()).append("\n");
        }

        return super.toString() + "\n" + messages.toString();
    }
}
