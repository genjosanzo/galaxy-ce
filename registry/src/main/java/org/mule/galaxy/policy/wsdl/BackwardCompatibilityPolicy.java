package org.mule.galaxy.policy.wsdl;

import java.util.Collection;

import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;

public class BackwardCompatibilityPolicy extends AbstractWsdlVersioningPolicy {
    private static final String WSDL_BACKWARD_COMPAT = "wsdl-backward-compat";

    public BackwardCompatibilityPolicy() {
        super(WSDL_BACKWARD_COMPAT, "WSDL: Backward Compatability", "Enforces restrictions to ensure all new WSDL versions are backward compatabile.");
    }

    protected void check(Collection<ApprovalMessage> messages, DifferenceEvent event) {
        if (!event.isBackwardCompatabile()) {
            messages.add(new ApprovalMessage(event.getDescription()));
        }
    }
}
