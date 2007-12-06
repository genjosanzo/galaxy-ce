package org.mule.galaxy;

import java.util.Collection;
import java.util.ResourceBundle;

import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class ArtifactPolicyException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(ArtifactPolicyException.class);
    
    private Collection<Approval> approvals;

    public ArtifactPolicyException(Collection<Approval> approvals) {
        super(new Message("ARTIFACT_NOT_APPROVED", BUNDLE));
        this.approvals = approvals;
    }

    public Collection<Approval> getApprovals() {
        return approvals;
    }
    
}
