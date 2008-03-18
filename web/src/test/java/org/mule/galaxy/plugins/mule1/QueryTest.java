package org.mule.galaxy.plugins.mule1;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.util.Set;

public class QueryTest extends AbstractGalaxyTest {

    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        importHelloWsdl();

        // Import a document which should now be indexed
        Artifact muleArtifact = importHelloMule();

        Workspace workspace = muleArtifact.getParent();

        // Try out search!
        Set results = registry.search(new Query(Artifact.class).workspaceId(workspace.getId())).getResults();

        assertEquals(2, results.size());
        results = registry.search(new Query(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(Restriction.eq("mule.descriptor", "GreeterUMO"))
                 .add(Restriction.eq("documentType", Constants.MULE_QNAME))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
            .workspacePath(workspace.getPath())
                 .add(Restriction.like("mule.descriptor", "Greeter"))).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule.descriptor in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule.descriptor in ('GreeterUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule.descriptor in ('Bleh')", 0, 100).getResults();
        assertEquals(0, results.size());

        results = registry.search("select artifact from '/Default Workspace' where mule.descriptor in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact from '/Foo' where mule.descriptor in ('GreeterUMO', 'FooUMO')", 0, 100).getResults();
        assertEquals(0, results.size());

        results = registry.search("select artifact where mule.endpoint = 'Greeter.in'", 0, 100).getResults();
        assertEquals(1, results.size());

        results = registry.search("select artifact where mule.endpoint = 'Greeter.xx'", 0, 100).getResults();
        assertEquals(0, results.size());

        results = registry.search("select artifact where mule.model = 'main'", 0, 100).getResults();
        assertEquals(1, results.size());
        
        results = registry.search("select artifact where mule.model = 'main' and mule.endpoint = 'Greeter.in'", 0, 100).getResults();
        assertEquals(1, results.size());
        

        results = registry.search(new Query(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(Restriction.not(Restriction.eq("documentType", Constants.MULE_QNAME)))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(Restriction.like("mule.descriptor", "Greeter"))).getResults();
        assertEquals(1, results.size());

    }

}