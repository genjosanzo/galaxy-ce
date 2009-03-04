package org.mule.galaxy.impl;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrVersion;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.IOUtils;

import org.w3c.dom.Document;

public class ArtifactTest extends AbstractGalaxyTest {
    public void testMove() throws Exception {
        Artifact a = importHelloWsdl();
        
        Workspace w = registry.newWorkspace("test");
        
        registry.move(a, w.getPath(), a.getName());
        
        assertEquals(w.getId(), a.getParent().getId());
        
        Set results = registry.search(new Query(Artifact.class).fromId(w.getId())).getResults();
        
        assertEquals(1, results.size());
        
        // test moving it into the workspace its already in.
        registry.move(a, w.getPath(), a.getName());
    }
    
    public void testRename() throws Exception {
        Artifact a = importHelloWsdl();
        
        a.setName("test.wsdl");
        
        List<Item> artifacts = a.getParent().getItems();
        assertEquals(1, artifacts.size());
        
        Artifact a2 = (Artifact) artifacts.iterator().next();
        assertEquals("test.wsdl", a2.getName());
        
        a2.getDefaultOrLastVersion().setVersionLabel("2.0");
        
        ArtifactVersion v = (ArtifactVersion) registry.getItemById(a2.getDefaultOrLastVersion().getId());
        assertEquals("2.0", v.getVersionLabel());
    }

    public void testWorkspaces() throws Exception {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Workspace newWork = registry.newWorkspace("New Workspace");
        assertEquals("New Workspace", newWork.getName());
        assertNotNull(newWork.getId());
        
        try {
            registry.newWorkspace("New Workspace");
            fail("Two workspaces with the same name");
        } catch (DuplicateItemException e) {
        }
        
        Workspace child = newWork.newWorkspace("Child");
        assertEquals("Child", child.getName());
        assertNotNull(child.getId());
        assertNotNull(child.getUpdated());
        
        assertEquals(1, newWork.getWorkspaces().size());
        
        newWork.delete();
        
        assertEquals(1, registry.getWorkspaces().size());
        
        Workspace root = workspaces.iterator().next();
        child = root.newWorkspace("child");
        
        Workspace newRoot = registry.newWorkspace("newroot");
        registry.save(child, newRoot.getId());
        
        Collection<Workspace> children = newRoot.getWorkspaces();
        assertEquals(1, children.size());
        
        child = children.iterator().next();
        assertNotNull(child.getParent());
        
        registry.save(newRoot, "root");
        
        Workspace newWorkspace = newRoot.newWorkspace("child2");
        
        registry.save(newWorkspace, "root");
        
        assertNull(newWorkspace.getParent());
    }
    
    public void testAddDuplicate() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        workspace.createArtifact("application/wsdl+xml", "hello_world.wsdl", "0.1", helloWsdl);
        
        helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        try {
            workspace.createArtifact("application/wsdl+xml", "hello_world.wsdl", "0.1", helloWsdl);
            fail("Expected a duplicate item exception");
        } catch (DuplicateItemException e) {
            // great! expected
        }
        
        Collection<Item> artifacts = workspace.getItems();
        assertEquals(1, artifacts.size());
    }
    
    public void testAddWsdlWithApplicationOctetStream() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult ar = workspace.createArtifact("application/octet-stream", 
                                                    "hello_world.wsdl", "0.1", helloWsdl);
        
        Artifact artifact = (Artifact) ar.getEntry();
        
        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
    }   
    
    public void testAddMuleConfig() throws Exception {
        InputStream helloMule = getResourceAsStream("/mule/hello-config.xml");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        // Try application/xml
        EntryResult ar = workspace.createArtifact("application/xml", 
                                                    "hello_world.xml", "0.1", helloMule);
        
        Artifact artifact = (Artifact) ar.getEntry();
        
        assertEquals("application/xml", artifact.getContentType().toString());
        assertEquals("mule-configuration", artifact.getDocumentType().getLocalPart());
        

        // Try application/octent-stream
        helloMule = getResourceAsStream("/mule/hello-config.xml");
        ar = workspace.createArtifact("application/octet-stream", "hello_world2.xml", "0.1",
                                     helloMule);
        
        artifact = (Artifact) ar.getEntry();
        
        assertEquals("application/xml", artifact.getContentType().toString());
        assertEquals("mule-configuration", artifact.getDocumentType().getLocalPart());
    }
    
    public void testAddWsdl() throws Exception {
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult ar = workspace.createArtifact("application/wsdl+xml", 
                                                  "hello_world.wsdl", "0.1", 
                                                  helloWsdl);
        
        Artifact artifact = (Artifact) ar.getEntry();

        assertTrue(waitForIndexing((ArtifactVersion)ar.getEntryVersion()));
        assertNotNull(artifact.getId());
        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
        assertNotNull(artifact.getDocumentType());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart());
        
        Collection<? extends EntryVersion> versions = artifact.getVersions();
        assertNotNull(versions);
        assertEquals(1, versions.size());
        
        // test properties
        JcrVersion version = (JcrVersion) versions.iterator().next();
        boolean testedTNS = false;
        for (PropertyInfo next : version.getProperties()) {
            if (next.getName().equals("wsdl.targetNamespace")) {
                assertEquals("wsdl.targetNamespace", next.getName());
                assertNotNull(next.getValue());
                assertTrue(next.isLocked());
                assertTrue(next.isVisible());
                
                assertEquals("WSDL Target Namespace", next.getDescription());
                testedTNS = true;
            }
        }
        
        Calendar origUpdated = version.getUpdated();
        assertNotNull(origUpdated);
        
        assertTrue(testedTNS);

        // This is odd, but otherwise the updates happen too fast, and the lastUpdated tstamp isn't changed
        Thread.sleep(500);

        version.setLocked("wsdl.targetNamespace", true);
        version.setVisible("wsdl.targetNamespace", false);
        PropertyInfo pi = version.getPropertyInfo("wsdl.targetNamespace");
        assertTrue(pi.isLocked());
        assertFalse(pi.isVisible());
        
        Calendar update = version.getUpdated();
        assertTrue(update.after(origUpdated));
        
        artifact.setProperty("foo", "bar");
        assertEquals("bar", artifact.getProperty("foo"));
        
        // Test the version history
        Node node = version.getNode();
        assertEquals("0.1", node.getName());
        
        assertTrue(version.getData() instanceof Document);
        assertEquals("0.1", version.getVersionLabel());
        assertNotNull(version.getAuthor());
        assertTrue(version.isLatest());
        assertEquals("Created", getPhase(version).getName());
        
        Calendar created = version.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        version.setProperty("foo", "bar");
        assertEquals("bar", version.getProperty("foo"));
         
        // Create another version
        InputStream stream = version.getStream();
        assertNotNull(stream);
        
        InputStream helloWsdl2 = getResourceAsStream("/wsdl/hello.wsdl");
        
        ar = artifact.newVersion(helloWsdl2, "0.2");
        assertTrue(waitForIndexing((ArtifactVersion)ar.getEntryVersion()));
        JcrVersion newVersion = (JcrVersion) ar.getEntryVersion();
        assertTrue(newVersion.isLatest());
        assertFalse(version.isLatest());
        
        assertSame(newVersion, ar.getEntry().getDefaultOrLastVersion());
        
        versions = artifact.getVersions();
        assertEquals(2, versions.size());
        
        assertEquals("0.2", newVersion.getVersionLabel());
        assertEquals("Created", getPhase(newVersion).getName());
        
        stream = newVersion.getStream();
        assertNotNull(stream);
        assertTrue(stream.available() > 0);
        assertNotNull(newVersion.getAuthor());
        
        newVersion.setProperty("foo2", "bar");
        assertEquals("bar", newVersion.getProperty("foo2"));
        assertNull(version.getProperty("foo2"));
        
        Artifact a2 = (Artifact) registry.resolve(workspace, artifact.getName());
        assertNotNull(a2);
        
        version.setAsDefaultVersion();
        
        assertEquals(2, a2.getVersions().size());
        EntryVersion activeVersion = a2.getDefaultOrLastVersion();
        assertEquals("0.1", activeVersion.getVersionLabel());
        
        activeVersion.delete();
        
        assertEquals(1, a2.getVersions().size());
        
        activeVersion = a2.getDefaultOrLastVersion();
        assertNotNull(activeVersion);
        
        assertTrue(((JcrVersion)activeVersion).isLatest());
        
        Collection<Item> artifacts = a2.getParent().getItems();
        boolean found = false;
        for (Item a : artifacts) {
            if (a.getId().equals(a2.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        a2.delete();
    }

    /**
     * Test for http://mule.mulesource.org/jira/browse/GALAXY-54 .
     */
    public void testActiveVersion() throws Exception
    {
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());

        Workspace workspace = workspaces.iterator().next();

        String version1 = "This is version 1";
        String version2 = "This is version 2";

        ByteArrayInputStream bais = new ByteArrayInputStream(version1.getBytes("UTF-8"));

        EntryResult ar = workspace.createArtifact("text/plain",
                                                     "test.txt",
                                                     "1",
                                                     bais);
        assertNotNull(ar);
        assertTrue(ar.isApproved());

        bais = new ByteArrayInputStream(version2.getBytes());

        final Artifact artifact = (Artifact) ar.getEntry();

        ar = artifact.newVersion(bais, "2");
        assertNotNull(ar);
        assertTrue(ar.isApproved());

        assertNotNull(ar.getEntryVersion().getPrevious());
        
        artifact.getVersion("1").setAsDefaultVersion();

        Artifact a = (Artifact) registry.resolve(workspace, "test.txt");
        assertNotNull(a);
        assertEquals("1", a.getDefaultOrLastVersion().getVersionLabel());
        assertEquals(version1, IOUtils.readStringFromStream(((ArtifactVersion)a.getDefaultOrLastVersion()).getStream()));

        ArtifactVersion artifactVersion = (ArtifactVersion) a.getVersion("2");
        assertEquals(version2, IOUtils.readStringFromStream(artifactVersion.getStream()));
    }


    public void testAddNonUnderstood() throws Exception {
        InputStream logProps = getResourceAsStream("/log4j.properties");
        
        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();
        
        EntryResult ar = workspace.createArtifact("text/plain", 
                                                     "log4j.properties", 
                                                     "0.1", 
                                                     logProps);
        
        assertNotNull(ar);
    }

}
