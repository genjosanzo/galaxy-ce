package org.mule.galaxy.policy;

import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.SystemUtils;

public class PolicyException extends GalaxyException {

    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(PolicyException.class);
    
    private List<ApprovalMessage> approvals = Collections.emptyList();

    public PolicyException(List<ApprovalMessage> approvals) {
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
            messages.append(approval.toString()).append(SystemUtils.LINE_SEPARATOR);
        }

        return super.toString() + SystemUtils.LINE_SEPARATOR + messages.toString();
    }
}
