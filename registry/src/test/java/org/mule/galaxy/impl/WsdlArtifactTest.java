package org.mule.galaxy.impl;


import java.io.InputStream;
import java.util.Calendar;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.w3c.dom.Document;

public class WsdlArtifactTest extends AbstractGalaxyTest {
    public void testAddWsdl() throws Exception {
        Item av = importHelloWsdl();
        assertNotNull(av.getId());

        Phase p = av.getProperty(Registry.PRIMARY_LIFECYCLE);
        assertNotNull(p);
        
        p = av.getParent().getProperty(Registry.PRIMARY_LIFECYCLE);
        assertNull(p);
        
        Artifact artifact = av.getProperty("artifact");
        assertNotNull(artifact);
        assertEquals("application/xml", artifact.getContentType().toString());
        assertNotNull(artifact.getDocumentType());
        assertEquals("definitions", artifact.getDocumentType().getLocalPart());
        
        // test properties
        boolean testedTNS = false;
        for (PropertyInfo next : av.getProperties()) {
            if (next.getName().equals("wsdl.targetNamespace")) {
                assertEquals("wsdl.targetNamespace", next.getName());
                assertNotNull(next.getValue());
                assertTrue(next.isLocked());
                assertTrue(next.isVisible());
                
                assertEquals("WSDL Target Namespace", next.getDescription());
                testedTNS = true;
            }
        }
        
        Calendar origUpdated = av.getUpdated();
        assertNotNull(origUpdated);
        
        assertTrue(testedTNS);

        // This is odd, but otherwise the updates happen too fast, and the lastUpdated tstamp isn't changed
        Thread.sleep(500);

        av.setLocked("wsdl.targetNamespace", true);
        av.setVisible("wsdl.targetNamespace", false);
        PropertyInfo pi = av.getPropertyInfo("wsdl.targetNamespace");
        assertTrue(pi.isLocked());
        assertFalse(pi.isVisible());
        
        Calendar update = av.getUpdated();
        assertTrue(update.after(origUpdated));
        
        av.setProperty("foo", "bar");
        assertEquals("bar", av.getProperty("foo"));
        
        // Test the version history
        final Object data = artifact.getData();
        assertTrue(data instanceof Document);
        assertNotNull(av.getAuthor());
        assertEquals("Created", getPhase(av).getName());
        
        Calendar created = av.getCreated();
        assertTrue(created.getTime().getTime() > 0);
        
        av.setProperty("foo", "bar");
        assertEquals("bar", av.getProperty("foo"));
         
        // Create another version
        InputStream stream = artifact.getInputStream();
        assertNotNull(stream);
        stream.close();
        
//        InputStream helloWsdl2 = getResourceAsStream("/wsdl/hello.wsdl");
//        
//        ar = artifact.newVersion(helloWsdl2, "0.2");
//        assertTrue(waitForIndexing((ArtifactVersion)ar.getEntryVersion()));
//        JcrVersion newVersion = (JcrVersion) ar.getEntryVersion();
//        assertTrue(newVersion.isLatest());
//        assertFalse(version.isLatest());
//        
//        assertSame(newVersion, ar.getItem().getDefaultOrLastVersion());
//        
//        versions = artifact.getVersions();
//        assertEquals(2, versions.size());
//        
//        assertEquals("0.2", newVersion.getVersionLabel());
//        assertEquals("Created", getPhase(newVersion).getName());
//        
//        stream = newVersion.getStream();
//        assertNotNull(stream);
//        assertTrue(stream.available() > 0);
//        stream.close();
//        
//        assertNotNull(newVersion.getAuthor());
//        
//        newVersion.setProperty("foo2", "bar");
//        assertEquals("bar", newVersion.getProperty("foo2"));
//        assertNull(version.getProperty("foo2"));
//        
//        ArtifactImpl a2 = (ArtifactImpl) registry.resolve(workspace, artifact.getName());
//        assertNotNull(a2);
//        
//        version.setAsDefaultVersion();
//        
//        assertEquals(2, a2.getVersions().size());
//        EntryVersion activeVersion = a2.getDefaultOrLastVersion();
//        assertEquals("0.1", activeVersion.getVersionLabel());
//        
//        activeVersion.delete();
//        
//        assertEquals(1, a2.getVersions().size());
//        
//        activeVersion = a2.getDefaultOrLastVersion();
//        assertNotNull(activeVersion);
//        
//        assertTrue(((JcrVersion)activeVersion).isLatest());
//        
//        Collection<Item> artifacts = a2.getParent().getItems();
//        boolean found = false;
//        for (Item a : artifacts) {
//            if (a.getId().equals(a2.getId())) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found);
//        a2.delete();
    }
}
