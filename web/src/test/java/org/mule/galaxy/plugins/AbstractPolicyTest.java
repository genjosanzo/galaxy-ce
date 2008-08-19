/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.plugins;

import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractPolicyTest extends AbstractGalaxyTest
{
    public void doPolicyTest(InputStream stream, String policyId, boolean fail) throws Exception
    {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        Policy p = policyManager.getPolicy(policyId);
        assertNotNull(p);

        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), p);

        try
        {
            workspace.createArtifact("application/xml",
                    "http-policy-test.xml", "0.1", stream);
            if (fail)
            {
                fail("Expected ArtifactPolicyException");
            }
        }
        catch (PolicyException e)
        {
            if (!fail)
            {
                Map<Item, List<ApprovalMessage>> approvals = e.getPolicyFailures();
                int count = 0;
                for (List<ApprovalMessage> msgs : approvals.values()) {
                    count += msgs.size();
                    for (ApprovalMessage a : msgs) {
                        System.out.println(a.getMessage());
                    }
                }
                
                assertEquals(1, count);

                ApprovalMessage a = approvals.values().iterator().next().get(0);
                assertFalse(a.isWarning());
                assertNotNull(a.getMessage());
            }
        }

    }
}
