package org.mule.galaxy.plugins.mule2;

import org.mule.galaxy.mule2.policy.RequireNoClientRemotingPolicy;

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