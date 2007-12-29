package org.mule.galaxy.policy.wsdl;

import java.util.Collection;

import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;

public class ForwardCompatibilityPolicy extends AbstractWsdlVersioningPolicy {
    private static final String WSDL_FORWARD_COMPAT = "wsdl-forward-compat";

    public String getId() {
        return WSDL_FORWARD_COMPAT;
    }
    public String getDescription() {
        return "Enforces restrictions to ensure all new WSDL versions are forward compatabile.";
    }

    public String getName() {
        return "WSDL Forward Compatability";
    }

    protected void check(Collection<ApprovalMessage> messages, DifferenceEvent event) {
        if (!event.isForwardCompatabile()) {
            messages.add(new ApprovalMessage(event.getDescription()));
        }
    }
}
