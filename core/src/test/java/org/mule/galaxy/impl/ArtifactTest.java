package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.wsdl.Definition;

import org.w3c.dom.Document;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.IOUtils;

public class ArtifactTest extends AbstractGalaxyTest {
    public void testWorkspaces() throws Exception {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Workspace newWork = registry.createWorkspace("New Workspace");
        assertEquals("New Workspace", newWork.getName());
        assertNotNull(newWork.getId());
        
        Workspace child = registry.createWorkspace(newWork, "Child");
        assertEquals("Child", child.getName());
        assertNotNull(child.getId());
        
        assertEquals(1, newWork.getWorkspaces().size());
        
        registry.deleteWorkspace(newWork.getId());
        
        assertEquals(1, registry.getWorkspaces().size());
        
        Workspace root = workspaces.iterator().next();
        child = registry.createWorkspace(root, "child");
        
        Workspace newRoot = registry.createWorkspace("newroot");
        registry.updateWorkspace(child, "child", newRoot.getId());
        
        Collection<Workspace> children = newRoot.getWorkspaces();
        assertEquals(1, children.size());
        
        child = children.iterator().next();
        assertNotNull(child.getParent());
    }
    
    public void testAddWsdl() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, "application/wsdl+xml", 
                                                    "hello_world.wsdl", "0.1", helloWsdl, getAdmin());
        
        Artifact artifact = ar.getArtifact();
        
        assertNotNull(artifact.getId());
        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
        assertNotNull(artifact.getDocumentType());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart());
        
        Set<? extends ArtifactVersion> versions = artifact.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        // test properties
        JcrVersion version = (JcrVersion) versions.iterator().next();
        Iterator<PropertyInfo> properties = version.getProperties(); 
        boolean testedTNS = false;
        while(properties.hasNext()) {
            PropertyInfo next = properties.next();
            if (next.getName().equals("wsdl.targetNamespace")) {
                assertEquals("wsdl.targetNamespace", next.getName());
                assertNotNull(next.getValue());
                assertTrue(next.isLocked());
                assertTrue(next.isVisible());
                
                assertEquals("WSDL Target Namespace", next.getDescription());
                assertTrue(next.isIndex());
                
                testedTNS = true;
            }
        }
        
        assertTrue(testedTNS);
        
        version.setLocked("wsdl.targetNamespace", true);
        version.setVisible("wsdl.targetNamespace", false);
        PropertyInfo pi = version.getPropertyInfo("wsdl.targetNamespace");
        assertTrue(pi.isLocked());
        assertFalse(pi.isVisible());
        
        artifact.setProperty("foo", "bar");
        assertEquals("bar", artifact.getProperty("foo"));
        
        // Test the version history
        Node node = version.getNode();
        assertEquals("version", node.getName());
        
        assertTrue(version.getData() instanceof Document);
        assertEquals("0.1", version.getVersionLabel());
        assertNotNull(version.getAuthor());
        assertTrue(version.isLatest());
        assertEquals("Created", artifact.getPhase().getName());
        
        Calendar created = version.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        assertEquals("bar", version.getProperty("foo"));
         
        // Create another version
        InputStream stream = version.getStream();
        assertNotNull(stream);
        
        InputStream helloWsdl2 = getResourceAsStream("/wsdl/hello.wsdl");
        
        ar = registry.newVersion(artifact, helloWsdl2, "0.2", getAdmin());
        JcrVersion newVersion = (JcrVersion) ar.getArtifactVersion();
        assertTrue(newVersion.isLatest());
        assertFalse(version.isLatest());
        
        versions = artifact.getVersions();
        assertEquals(2, versions.size());
        
        assertEquals("0.2", newVersion.getVersionLabel());
        
        stream = newVersion.getStream();
        assertNotNull(stream);
        assertTrue(stream.available() > 0);
        assertNotNull(newVersion.getAuthor());
        
        newVersion.setProperty("foo2", "bar");
        assertEquals("bar", newVersion.getProperty("foo2"));
        assertEquals("bar", artifact.getProperty("foo2"));
        assertNull(version.getProperty("foo2"));
        
        Artifact a2 = registry.getArtifact(workspace, artifact.getName());
        assertNotNull(a2);
    }
    
    public void testAddNonUnderstood() throws Exception {
        InputStream logProps = getResourceAsStream("/log4j.properties");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        ArtifactResult ar = registry.createArtifact(workspace, 
                                                    "text/palin", 
                                                    "log4j.properties", 
                                                    "0.1", 
                                                    logProps, 
                                                    getAdmin());
        
        assertNotNull(ar);
    }
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
