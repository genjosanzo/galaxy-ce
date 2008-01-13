package org.mule.galaxy.mule1;


import org.mule.galaxy.test.AbstractConfigLookupTest;

public class RegistryIndexesLookupTest extends AbstractConfigLookupTest
{
    public void testLookupByServerId() throws Exception
    {
        doLookup("select artifact where mule.server.id = 'hello-server'", 1);
    }

    //TODO Wildcards don't work
//    public void testLookupByDescription() throws Exception
//    {
//        doLookup("select artifact where mule2.description = '*hello sample application*'", 1);
//    }

    public void testLookupByServiceName() throws Exception
    {
        doLookup("select artifact where mule.descriptor = 'GreeterUMO'", 1);
    }

    public void testLookupByEndpointName() throws Exception
    {
        doLookup("select artifact where mule.endpoint = 'Greeter.in'", 1);
    }

    public void testLookupByBadEndpointName() throws Exception
    {
        doLookup("select artifact where mule.endpoint = 'Greeter.Xx'", 0);
    }

    public void testLookupByModelName() throws Exception
    {
        doLookup("select artifact where mule.model = 'main'", 1);
    }
}