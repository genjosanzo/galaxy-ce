package org.mule.galaxy.plugins.mule2;

import java.io.InputStream;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

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
            .fromId(workspace.getId())
                 .add(OpRestriction.eq("mule2.service", "GreeterUMO"))
                 .add(OpRestriction.eq("documentType", Constants.MULE2_2_QNAME))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query()
            .fromPath(workspace.getPath())
                 .add(OpRestriction.like("mule2.service", "Greeter"))).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule2.service in ('GreeterUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule2.service in ('Bleh')", 0, 100).getResults();
        assertEquals(0, results.size());

        results = registry.search("select artifact from '/Default Workspace' where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact from '/Foo' where mule2.service in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(0, results.size());


        results = registry.search(new Query()
            .fromId(workspace.getId())
                 .add(OpRestriction.not(OpRestriction.eq("documentType", Constants.MULE2_2_QNAME)))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query()
            .fromId(workspace.getId())
                 .add(OpRestriction.like("mule2.service", "Greeter"))).getResults();
        assertEquals(1, results.size());

    }

    protected Item importHelloMule2() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");

        return importFile(helloWsdl, "hello-config.xml", "0.1", "application/xml");
    }

}