package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.DOMUtils;

import org.w3c.dom.Document;

public class DocumentTest extends AbstractGalaxyTest {
    
    public void testAddWsdl() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        Artifact artifact = registry.createArtifact(workspace, "application/wsdl+xml", null, helloWsdl);
        
        assertNotNull(artifact.getId());
        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
        assertNotNull(artifact.getDocumentType());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart());
        
        Set<? extends ArtifactVersion> versions = artifact.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        // Test the version history
        ArtifactVersion version = versions.iterator().next();
        assertTrue(version.getData() instanceof Definition);
        assertEquals(settings.getInitialDocumentVersion(), version.getLabel());
        
        Calendar created = version.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        InputStream stream = version.getStream();
        assertNotNull(stream);
        
        
        InputStream helloWsdl2 = getResourceAsStream("/wsdl/hello.wsdl");
        ArtifactVersion newVersion = registry.newVersion(artifact, helloWsdl2);
        
        versions = artifact.getVersions();
        assertEquals(2, versions.size());
        
        stream = newVersion.getStream();
        assertNotNull(stream);
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
