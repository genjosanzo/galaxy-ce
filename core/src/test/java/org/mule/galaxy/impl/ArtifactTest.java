package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import javax.jcr.Node;
import javax.wsdl.Definition;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;

public class ArtifactTest extends AbstractGalaxyTest {
    
    public void testAddWsdl() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        Artifact artifact = registry.createArtifact(workspace, "application/wsdl+xml", null, "0.1", helloWsdl);
        
        assertNotNull(artifact.getId());
        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
        assertNotNull(artifact.getDocumentType());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart());
        
        Set<? extends ArtifactVersion> versions = artifact.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        // Test the version history
        JcrVersion version = (JcrVersion) versions.iterator().next();
        Node node = version.getNode();
        assertEquals("version", node.getName());
        
        assertTrue(version.getData() instanceof Definition);
        assertEquals("0.1", version.getVersionLabel());
        
        assertNull(artifact.getPhase());
        
        Calendar created = version.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        InputStream stream = version.getStream();
        assertNotNull(stream);
        
        InputStream helloWsdl2 = getResourceAsStream("/wsdl/hello.wsdl");
        ArtifactVersion newVersion = registry.newVersion(artifact, helloWsdl2, "0.2");
        
        versions = artifact.getVersions();
        assertEquals(2, versions.size());
        
        assertEquals("0.2", newVersion.getVersionLabel());
        
        stream = newVersion.getStream();
        assertNotNull(stream);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
