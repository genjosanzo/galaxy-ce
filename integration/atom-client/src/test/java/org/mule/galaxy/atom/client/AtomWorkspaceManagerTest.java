package org.mule.galaxy.atom.client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.type.TypeManager;

public class AtomWorkspaceManagerTest extends AbstractAtomTest {
    protected static final String WORKSPACE_NAME = "Foo";

    public void testArtifactCreation() throws Exception {
        Item w = attatchTestWorkspace();
        
        Item version = importFile(w, getClass().getResourceAsStream("/wsdl/hello.wsdl"), "hello.wsdl", "1.0", "application/xml");
        Item artifactItem = version.getParent();
        assertNotNull(artifactItem);
        assertEquals("1.0", version.getName());
        assertEquals("hello.wsdl", artifactItem.getName());
        
        Artifact artifact = (Artifact) version.getProperty("artifact");
        assertNotNull(artifact);
        assertEquals("application/xml", artifact.getContentType().toString());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart().toString());
        InputStream is = artifact.getInputStream();
        assertNotNull(is);
        is.close();
        
        assertEquals("1.0", version.getName());
        
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("artifact", new Object[] { getClass().getResourceAsStream("/wsdl/hello.wsdl"), "application/xml" });
        NewItemResult result = artifactItem.newItem("2.0", 
                                                    typeManager.getType(TypeManager.ARTIFACT_VERSION), 
                                                    props);

        assertNotNull(result);
        assertEquals(artifactItem, result.getItem().getParent());
        
        try {
            result = artifactItem.newItem("2.0", 
                                          typeManager.getType(TypeManager.ARTIFACT_VERSION), 
                                          props);
            fail("Expected Duplicate item exception");
        } catch (DuplicateItemException e) {
        }
    }

    public void testItemCreation() throws Exception {
        Item w = attatchTestWorkspace();
        
        NewItemResult result = w.newItem("FooService", typeManager.getType(TypeManager.VERSION));
        assertNotNull(result);
        
        Item item = result.getItem();
        assertTrue(item instanceof AtomItem);
        assertNotNull(item);
        assertEquals("FooService", item.getName());
        
        result = item.newItem("2.0", typeManager.getType(TypeManager.VERSIONED));
        assertNotNull(result);
        Item child = result.getItem();
        assertEquals("2.0", child.getName());
        assertNotNull(child.getType());
        assertEquals(TypeManager.VERSIONED, child.getType().getName());
        
        try {
            item.newItem("2.0", typeManager.getType(TypeManager.VERSIONED));
            fail("Expected Duplicate item exception");
        } catch (DuplicateItemException e) {
        }
    }

    public void testItems() throws Exception {
        Item w = attatchTestWorkspace();
        
        Item t1 = (Item) registry.getItemByPath("Test");
        Item t2 = t1.newItem("Test2", typeManager.getType(TypeManager.WORKSPACE)).getItem();
        t2.newItem("Test3", typeManager.getType(TypeManager.WORKSPACE)).getItem();
        
        // browse the workspaces we created locally
        List<Item> items = w.getItems();
        assertEquals(1, items.size());
        
        Item remoteT2 = (Item) items.iterator().next();
        items = remoteT2.getItems();
        assertEquals(1, items.size());

        Item remoteT3 = (Item) items.iterator().next();
        items = remoteT3.getItems();
        assertEquals(0, items.size());
        assertEquals("/parent/atom/Test2/Test3", remoteT3.getPath());
        
        Item anotherRemoteT2 = remoteT3.getParent();
        assertNotNull(anotherRemoteT2);
        assertEquals(remoteT2.getId(), anotherRemoteT2.getId());
        assertEquals("/parent/atom/Test2", anotherRemoteT2.getPath());

        Item attached = anotherRemoteT2.getParent();
        assertEquals("/parent/atom", attached.getPath());
        assertNotNull(attached);
        assertEquals(w.getId(), attached.getId());
    }

}
