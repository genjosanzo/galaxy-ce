package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.policy.AbstractPolicy;
import org.mule.galaxy.policy.ApprovalMessage;

public class AlwaysFailPolicy extends AbstractPolicy {

    public AlwaysFailPolicy() {
        super("faux", "Faux Policy", "Faux policy description");
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        return Arrays.asList(new ApprovalMessage("Not approved"));
    }
}