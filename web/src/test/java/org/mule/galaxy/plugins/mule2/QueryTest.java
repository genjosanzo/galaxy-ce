package org.mule.galaxy.plugins.mule2;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

import java.util.Set;
import java.util.Collection;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeTypeParseException;

public class QueryTest extends AbstractGalaxyTest {

    public void testQueries() throws Exception {
        // Import a document which should now be indexed
        importHelloWsdl();

        // Import a document which should now be indexed
        Artifact muleArtifact = importHelloMule2();

        Workspace workspace = muleArtifact.getWorkspace();

        // Try out search!
        Set results = registry.search(new Query(Artifact.class).workspaceId(workspace.getId())).getResults();

        assertEquals(2, results.size());

        results = registry.search(new Query(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(Restriction.eq("mule2.service", "GreeterUMO"))
                 .add(Restriction.eq("documentType", Constants.MULE2_QNAME))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
            .workspacePath(workspace.getPath())
                 .add(Restriction.like("mule2.service", "Greeter"))).getResults();
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
            .workspaceId(workspace.getId())
                 .add(Restriction.not(Restriction.eq("documentType", Constants.MULE2_QNAME)))).getResults();
        assertEquals(1, results.size());

        results = registry.search(new Query(Artifact.class)
            .workspaceId(workspace.getId())
                 .add(Restriction.like("mule2.service", "Greeter"))).getResults();
        assertEquals(1, results.size());

    }

    protected Artifact importHelloMule2() throws Exception
    {
        InputStream helloWsdl = getResourceAsStream("/mule2/hello-config.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult ar = registry.createArtifact(workspace,
                                                    "application/xml",
                                                    "hello-config.xml",
                                                    "0.1", helloWsdl, getAdmin());
        return ar.getArtifact();
    }

}