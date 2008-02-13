package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;

public class AlwaysFailArtifactPolicy implements ArtifactPolicy {
    public String getDescription() {
        return "Faux policy description";
    }

    public boolean applies(Artifact a) {
        return true;
    }

    public String getId() {
        return "faux";
    }

    public String getName() {
        return "Faux policy";
    }

    public Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        return Arrays.asList(new ApprovalMessage("Not approved"));
    }

    public void setRegistry(Registry registry) {
        
    }
}