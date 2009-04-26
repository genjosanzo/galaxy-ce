package org.mule.galaxy.plugins.mule2;

import org.mule.galaxy.mule2.policy.RequireSSLPolicy;

public class MuleRequireSSLPolicyTest extends AbstractPolicyTest
{

    public void testRequiresHttpsOnGlobalEndpoint() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/require-ssl-policy-test1.xml"), RequireSSLPolicy.ID, true);
    }

    public void testRequiresHttpsOnLocalEndpoint() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/require-ssl-policy-test2.xml"), RequireSSLPolicy.ID, true);
    }

    public void testRequiresHttpsOk() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/require-ssl-policy-test3.xml"), RequireSSLPolicy.ID, false);
    }
}