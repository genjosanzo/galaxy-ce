package org.mule.galaxy.plugins.mule1;

import org.mule.galaxy.mule1.policy.RequireNoClientRemotingPolicy;
import org.mule.galaxy.plugins.AbstractPolicyTest;

public class MuleNoClientRemotingPolicyTest extends AbstractPolicyTest
{
    public void testWithClientRemoting() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/no-client-remoting-policy-test1.xml"), RequireNoClientRemotingPolicy.ID, true);
    }

    public void testNoClientRemoting() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/no-client-remoting-policy-test2.xml"), RequireNoClientRemotingPolicy.ID, false);
    }
}