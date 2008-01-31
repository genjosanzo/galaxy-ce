/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api;

import org.mule.galaxy.api.policy.ApprovalMessage;

import java.util.Collection;

/**
 * TODO
 */

public class ArtifactResult
{
    private Artifact artifact;
    private ArtifactVersion artifactVersion;
    private Collection<ApprovalMessage> approvals;
    private boolean approved = true;

    public ArtifactResult(Artifact artifact,
                          ArtifactVersion artifactVersion,
                          Collection<ApprovalMessage> approvals)
    {
        super();
        this.artifact = artifact;
        this.artifactVersion = artifactVersion;
        this.approvals = approvals;

        for (ApprovalMessage a : approvals)
        {
            if (!a.isWarning())
            {
                approved = false;
                break;
            }
        }
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public ArtifactVersion getArtifactVersion()
    {
        return artifactVersion;
    }

    public Collection<ApprovalMessage> getApprovals()
    {
        return approvals;
    }

    public boolean isApproved()
    {
        return approved;
    }

}
