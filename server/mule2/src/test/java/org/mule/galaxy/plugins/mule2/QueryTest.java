package org.mule.galaxy.plugins.mule2;

import java.io.InputStream;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class QueryTest extends AbstractGalaxyTest {

    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        importHelloWsdl();

        // Import a document which should now be indexed
        Item muleArtifact = importHelloMule2();

        Item workspace = (Item) muleArtifact.getParent().getParent();

        // Try out search!
        Set results = registry.search(new Query().fromId(workspace.getId())).getResults();

        assertEquals(2, results.size());

        results = registry.search(new Query()
            .fromId(workspace.getId(), true)
                 .add(OpRestriction.eq("mule.service", "GreeterUMO"))).getResults();
        assertEquals(1, results.size());

        results = registry.search("select where mule.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select where mule.service in ('GreeterUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select where mule.service in ('Bleh')", 0, 100).getResults();
        assertEquals(0, results.size());

        results = registry.search("select from '/Default Workspace' recursive where mule.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select from '/Foo' recursive where mule.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(0, results.size());

    }

    protected Item importHelloMule2() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");

        return importFile(helloWsdl, "hello-config.xml", "0.1", "application/xml");
    }

}