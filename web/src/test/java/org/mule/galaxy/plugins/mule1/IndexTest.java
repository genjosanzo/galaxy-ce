package org.mule.galaxy.plugins.mule1;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Collection;

public class IndexTest extends AbstractGalaxyTest {


    public void testIndexes() throws Exception {

        // Import a document which should now be indexed
        InputStream helloConfig = getResourceAsStream("/mule/hello-config.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactResult ar = registry.createArtifact(workspace,
                                                    "application/xml",
                                                    "hello-config.xml",
                                                    "0.1", helloConfig, getAdmin());
        Artifact artifact = ar.getArtifact();

        JcrVersion version = (JcrVersion) artifact.getDefaultOrLastVersion();
        Object serverId = version.getProperty("mule.server.id");
        assertEquals("hello-server", serverId);
    }
}