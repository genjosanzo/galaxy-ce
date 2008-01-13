package org.mule.galaxy.mule2;


import org.mule.galaxy.test.AbstractConfigLookupTest;

public class RegistryIndexesLookupTest extends AbstractConfigLookupTest
{
    public void testLookupByServerId() throws Exception
    {
        doLookup("select artifact where mule2.server.id = 'hello-server'", 1);
    }

    //TODO Wildcards don't work
//    public void testLookupByDescription() throws Exception
//    {
//        doLookup("select artifact where mule2.description = '*hello sample application*'", 1);
//    }

    public void testLookupByServiceName() throws Exception
    {
        doLookup("select artifact where mule2.service = 'ChitChatUMO'", 1);
    }

    public void testLookupByEndpointName() throws Exception
    {
        doLookup("select artifact where mule2.endpoint = 'Greeter.in'", 1);
    }

    public void testLookupByBadEndpointName() throws Exception
    {
        doLookup("select artifact where mule2.endpoint = 'Greeter.Xx'", 0);
    }

    public void testLookupByModelName() throws Exception
    {
        doLookup("select artifact where mule2.model = 'helloSample'", 1);
    }
}