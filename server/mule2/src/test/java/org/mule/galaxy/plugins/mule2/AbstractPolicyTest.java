/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.plugins.mule2;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.test.AbstractGalaxyTest;

public abstract class AbstractPolicyTest extends AbstractGalaxyTest
{
    public void doPolicyTest(InputStream stream, String policyId, boolean fail) throws Exception
    {
        Policy p = policyManager.getPolicy(policyId);
        assertNotNull(p);

        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), p);

        try
        {
            importFile(stream, "policy-test.xml", "0.1", "application/xml");
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
