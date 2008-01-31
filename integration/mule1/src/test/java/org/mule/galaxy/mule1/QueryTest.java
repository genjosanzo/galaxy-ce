package org.mule.galaxy.mule1;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.query.QueryImpl;
import org.mule.galaxy.query.RestrictionImpl;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.api.util.Constants;

import java.util.Set;

public class QueryTest extends AbstractGalaxyTest {

    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        importHelloWsdl();

        // Import a document which should now be indexed
        Artifact muleArtifact = importHelloMule();

        Workspace workspace = muleArtifact.getWorkspace();

        // Try out search!
        Set results = registry.search(new QueryImpl(Artifact.class).workspaceId(workspace.getId())).getResults();

        assertEquals(2, results.size());

        results = registry.search(new QueryImpl(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(RestrictionImpl.eq("mule.descriptor", "GreeterUMO"))
                 .add(RestrictionImpl.eq("documentType", Constants.MULE_QNAME))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new QueryImpl(Artifact.class)
            .workspacePath(workspace.getPath())
                 .add(RestrictionImpl.like("mule.descriptor", "Greeter"))).getResults();
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

        results = registry.search(new QueryImpl(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(RestrictionImpl.not(RestrictionImpl.eq("documentType", Constants.MULE_QNAME)))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new QueryImpl(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(RestrictionImpl.like("mule.descriptor", "Greeter"))).getResults();
        assertEquals(1, results.size());

    }

}