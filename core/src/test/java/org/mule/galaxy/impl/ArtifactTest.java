package org.mule.galaxy.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.impl.artifact.ArtifactExtension;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.IOUtils;

public class ArtifactTest extends AbstractGalaxyTest {
    protected ArtifactExtension artifactExtension;
    
    public void testMove() throws Exception {
        Item a = importHelloWsdl();
        
        Item w = registry.newItem("test", typeManager.getTypeByName(TypeManager.WORKSPACE)).getItem();
        
        registry.move(a, w.getPath(), a.getName());
        
        assertEquals(w.getId(), a.getParent().getId());
        
        Set<Item> results = registry.search(new Query().fromId(w.getId())).getResults();
        
        assertEquals(1, results.size());
        
        // test moving it into the workspace its already in.
        registry.move(a, w.getPath(), a.getName());
    }
    
    public void testUpdate() throws Exception {
        Item i = importFile(new ByteArrayInputStream("test".getBytes()), "test.txt", "0.1", "text/plain");
        
        i.setProperty("artifact", new Object[] { new ByteArrayInputStream("test2".getBytes()), "text/plain" });
        registry.save(i);
        
        Artifact a = (Artifact) i.getProperty("artifact");
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        IOUtils.copy(a.getInputStream(), byteStream);
        assertEquals("test2", new String(byteStream.toByteArray()));
    }
    

    public void testDelete() throws Exception {
        Item i = importFile(new ByteArrayInputStream("test".getBytes()), "test.txt", "0.1", "text/plain");
        i.delete();
        
        assertEquals(0, artifactExtension.getArtifactCount());
        
        i = importFile(new ByteArrayInputStream("test".getBytes()), "test2.txt", "0.1", "text/plain");
        i.getParent().delete();
        
        assertEquals(0, artifactExtension.getArtifactCount());
    }
    
    
    public void testRename() throws Exception {
        Item a = importHelloWsdl();
        a.setName("2.0");
        
        List<Item> artifacts = a.getParent().getItems();
        assertEquals(1, artifacts.size());
        
        Item a2 = (Item) artifacts.iterator().next();
        assertEquals("2.0", a2.getName());
    }
//
//    public void testWorkspaces() throws Exception {
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        
//        Item newWork = registry.newItem("New Workspace", type);
//        assertEquals("New Workspace", newWork.getName());
//        assertNotNull(newWork.getId());
//        
//        try {
//            registry.newItem("New Workspace", type);
//            fail("Two workspaces with the same name");
//        } catch (DuplicateItemException e) {
//        }
//        
//        Item child = newWork.newItem("Child", type);
//        assertEquals("Child", child.getName());
//        assertNotNull(child.getId());
//        assertNotNull(child.getUpdated());
//        
//        assertEquals(1, newWork.getChildren().size());
//        
//        newWork.delete();
//        
//        assertEquals(1, registry.getItems().size());
//        
//        Item root = workspaces.iterator().next();
//        child = root.newItem("child", type);
//        
//        Item newRoot = registry.newItem("newroot", type);
//        registry.save(child, newRoot.getId());
//        
//        Collection<Item> children = newRoot.getChildren();
//        assertEquals(1, children.size());
//        
//        child = children.iterator().next();
//        assertNotNull(child.getParent());
//        
//        registry.save(newRoot, "root");
//        
//        Item newWorkspace = newRoot.newItem("child2", type);
//        
//        registry.save(newWorkspace, "root");
//        
//        assertNull(newWorkspace.getParent());
//    }
//    
//    public void testAddDuplicate() throws Exception {
//        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
//        
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        Item workspace = workspaces.iterator().next();
//        
//        workspace.createArtifact("application/wsdl+xml", "hello_world.wsdl", "0.1", helloWsdl);
//        
//        helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
//        try {
//            workspace.createArtifact("application/wsdl+xml", "hello_world.wsdl", "0.1", helloWsdl);
//            fail("Expected a duplicate item exception");
//        } catch (DuplicateItemException e) {
//            // great! expected
//        }
//        
//        Collection<Item> artifacts = workspace.getItems();
//        assertEquals(1, artifacts.size());
//    }
//    
//    public void testAddWsdlWithApplicationOctetStream() throws Exception {
//        InputStream helloWsdl = getResourceAsStream("/wsdl/hello.wsdl");
//        
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        Item workspace = workspaces.iterator().next();
//        
//        NewItemResult ar = workspace.createArtifact("application/octet-stream", 
//                                                    "hello_world.wsdl", "0.1", helloWsdl);
//        
//        ArtifactImpl artifact = (ArtifactImpl) ar.getItem();
//        
//        assertEquals("application/wsdl+xml", artifact.getContentType().toString());
//    }   
//    
//    public void testAddMuleConfig() throws Exception {
//        InputStream helloMule = getResourceAsStream("/mule/hello-config.xml");
//        
//        Collection<Item> workspaces = registry.getItems();
//        assertEquals(1, workspaces.size());
//        Item workspace = workspaces.iterator().next();
//        
//        // Try application/xml
//        NewItemResult ar = workspace.createArtifact("application/xml", 
//                                                    "hello_world.xml", "0.1", helloMule);
//        
//        ArtifactImpl artifact = (ArtifactImpl) ar.getItem();
//        
//        assertEquals("application/xml", artifact.getContentType().toString());
//        assertEquals("mule-configuration", artifact.getDocumentType().getLocalPart());
//        
//
//        // Try application/octent-stream
//        helloMule = getResourceAsStream("/mule/hello-config.xml");
//        ar = workspace.createArtifact("application/octet-stream", "hello_world2.xml", "0.1",
//                                     helloMule);
//        
//        artifact = (ArtifactImpl) ar.getItem();
//        
//        assertEquals("application/xml", artifact.getContentType().toString());
//        assertEquals("mule-configuration", artifact.getDocumentType().getLocalPart());
//    }
    
}
