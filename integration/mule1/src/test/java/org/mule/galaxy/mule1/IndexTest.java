package org.mule.galaxy.mule1;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactResult;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Index;
import org.mule.galaxy.api.PropertyInfo;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.util.Constants;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.query.QueryImpl;
import org.mule.galaxy.query.RestrictionImpl;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

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

        JcrVersion version = (JcrVersion) artifact.getActiveVersion();
        Object property = version.getProperty("mule.description");
        //TODO RM* revist after the refactor assertNotNull(property);
        //assertTrue(property instanceof String);
    }
}