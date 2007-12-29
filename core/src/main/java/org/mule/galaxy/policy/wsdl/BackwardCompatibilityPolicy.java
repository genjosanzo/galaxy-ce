package org.mule.galaxy.policy.wsdl;

import java.util.Collection;
import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

public class BackwardCompatibilityPolicy extends AbstractWsdlVersioningPolicy {
    private static final String WSDL_BACKWARD_COMPAT = "wsdl-backward-compat";

    public String getId() {
        return WSDL_BACKWARD_COMPAT;
    }
    
    public String getDescription() {
        return "Enforces restrictions to ensure all new WSDL versions are backward compatabile.";
    }

    public String getName() {
        return "WSDL Backward Compatability";
    }

    protected void check(Collection<ApprovalMessage> messages, DifferenceEvent event) {
        if (!event.isBackwardCompatabile()) {
            messages.add(new ApprovalMessage(event.getDescription()));
        }
    }
}
