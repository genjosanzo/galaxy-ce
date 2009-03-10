package org.mule.galaxy.plugins.mule2;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

public class QueryTest extends AbstractGalaxyTest {

    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        importHelloWsdl();

        // Import a document which should now be indexed
        Artifact muleArtifact = importHelloMule2();

        Workspace workspace = (Workspace) muleArtifact.getParent();

        // Try out search!
        Set results = registry.search(new Query(Artifact.class).fromId(workspace.getId())).getResults();

        assertEquals(2, results.size());

        results = registry.search(new Query(Artifact.class)
            .fromId(workspace.getId())
                 .add(OpRestriction.eq("mule2.service", "GreeterUMO"))
                 .add(OpRestriction.eq("documentType", Constants.MULE2_2_QNAME))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
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


        results = registry.search(new Query(Artifact.class)
            .fromId(workspace.getId())
                 .add(OpRestriction.not(OpRestriction.eq("documentType", Constants.MULE2_2_QNAME)))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
            .fromId(workspace.getId())
                 .add(OpRestriction.like("mule2.service", "Greeter"))).getResults();
        assertEquals(1, results.size());

    }

    protected Artifact importHelloMule2() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        EntryResult ar = workspace.createArtifact("application/xml",
                                                     "hello-config.xml",
                                                     "0.1", 
                                                     helloWsdl);
        return (Artifact) ar.getEntry();
    }

}