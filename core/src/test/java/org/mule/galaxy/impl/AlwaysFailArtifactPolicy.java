package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;

public class AlwaysFailArtifactPolicy implements Policy {
    public String getDescription() {
        return "Faux policy description";
    }

    public boolean applies(Item item) {
        return true;
    }

    public String getId() {
        return "faux";
    }

    public String getName() {
        return "Faux policy";
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        return Arrays.asList(new ApprovalMessage("Not approved"));
    }

    public void setRegistry(Registry registry) {
        
    }
}