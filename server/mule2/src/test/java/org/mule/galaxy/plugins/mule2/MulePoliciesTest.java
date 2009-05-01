package org.mule.galaxy.plugins.mule2;

import org.mule.galaxy.mule2.policy.RequireGlobalEndpointsPolicy;
import org.mule.galaxy.mule2.policy.RequireNoClientRemotingPolicy;
import org.mule.galaxy.mule2.policy.RequireSSLPolicy;

public class MulePoliciesTest extends AbstractPolicyTest
{
    public void testInbound() throws Exception
    {
        doPolicyTest(getResourceAsStream("/local-inbound-endpoints.xml"), RequireGlobalEndpointsPolicy.ID, true);
    }

    public void testOutbound() throws Exception
    {
        doPolicyTest(getResourceAsStream("/local-outbound-endpoints.xml"), RequireGlobalEndpointsPolicy.ID, true);
    }
    
    public void testWithClientRemoting() throws Exception
    {
        doPolicyTest(getResourceAsStream("/no-client-remoting-policy-test1.xml"), RequireNoClientRemotingPolicy.ID, true);
    }
    
    public void testRequiresHttpInbound() throws Exception
    {
        doPolicyTest(getResourceAsStream("/require-ssl-policy-test1.xml"), RequireSSLPolicy.ID, true);
    }

    public void testRequiresHttpOutbound() throws Exception
    {
        doPolicyTest(getResourceAsStream("/require-ssl-policy-test2.xml"), RequireSSLPolicy.ID, true);
    }

    public void testRequiresHttpNamespace() throws Exception
    {
        doPolicyTest(getResourceAsStream("/require-ssl-policy-test3.xml"), RequireSSLPolicy.ID, false);
    }


    public void testRequiresSslNamespace() throws Exception
    {
        doPolicyTest(getResourceAsStream("/require-ssl-policy-test4.xml"), RequireSSLPolicy.ID, false);
    }

}