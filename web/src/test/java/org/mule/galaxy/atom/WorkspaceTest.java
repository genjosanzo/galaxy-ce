package org.mule.galaxy.atom;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class WorkspaceTest extends AbstractAtomTest {
    
    public void testRootWorkspaceManipulation() throws Exception {
        testWorkspace("http://localhost:9002/api/registry",  "/api/registry", 1);
    }
    
    public void testWorkspaceManipulation() throws Exception {
        testWorkspace("http://localhost:9002/api/registry/Default%20Workspace", "/api/registry/Default%20Workspace", 0);
    }
    
    private void testWorkspace(String dwBase, String relBase, int entries)
        throws IOException {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        // Grab workspaces & collections
        ClientResponse res = client.get(dwBase, defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());
        
        Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        assertEquals(entries, feed.getEntries().size());
        res.release();
        
        Entry entry = createEntry("MyWorkspace", "Workspace");
        prettyPrint(entry);
        res = client.post(dwBase, entry, defaultOpts);
        assertEquals(201, res.getStatus());
        Document<Entry> entryDoc = res.getDocument();
        entry = entryDoc.getRoot();
        assertEquals("MyWorkspace", entry.getTitle());
        prettyPrint(entry);
        Collection itemsResponse = null;
        for (Element e : entry.getExtensions()) {
            if (e instanceof Collection) {
                itemsResponse = (Collection) e;
            }
        }
        assertNotNull(itemsResponse);
        
        Element wInfo = entry.getExtension(new QName(ItemCollection.NAMESPACE, "item-info"));
        assertNotNull(wInfo);
        assertEquals("MyWorkspace", wInfo.getAttributeValue("name"));
        assertEquals("Workspace", wInfo.getAttributeValue("type"));
        
        assertEquals(1, entry.getAuthors().size());
        assertEquals(relBase + "/MyWorkspace;atom", entry.getLink("edit").getHref().toString());
        res.release();
        
        res = client.get(dwBase + "/MyWorkspace", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        res = client.get(dwBase + "/MyWorkspace;atom", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // Add an item to the new workspace
        entry = createEntry("Test", "Base Type");
        res = client.post(dwBase + "/MyWorkspace", entry, defaultOpts);
        assertEquals(201, res.getStatus());
        
        // Grab the new workspace's feed
        res = client.get(dwBase + "/MyWorkspace", defaultOpts);
        feedDoc = res.getDocument();
        prettyPrint(feedDoc);
        feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
        res.release();
        
        res = client.get(dwBase + "/MyWorkspace;atom", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // delete the workspace
        res = client.delete(dwBase + "/MyWorkspace;atom", defaultOpts);
        assertEquals(204, res.getStatus());
        
        res.release();        
    }
    
    public void testWorkspaceDoesntExist() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String base = "http://localhost:9002/api/registry/Doesntexist";
        // Grab workspaces & collections
        ClientResponse res = client.get(base, defaultOpts);
        
        assertEquals(404, res.getStatus());
        res.release();
    }
    
    public void testCreationOfAnExistingWorkspace() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        // Create a workspace which already exists
        Entry entry = createEntry("Default Workspace", "Workspace");
        
        ClientResponse res = client.post("http://localhost:9002/api/registry", entry, defaultOpts);
        
        assertEquals(409, res.getStatus());
        
        res.release();
    }
    
    public void testProperties() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        String base = "http://localhost:9002/api/registry/Default%20Workspace";
        
        // Create an entry
        System.out.println("Creating Entry for " + base);

        Entry entry = createEntry("MyService", "Base Type");
        ClientResponse res = client.post(base, entry, defaultOpts);
        entry = assertAndGetEntry(res, 201);
        
        entry.setSummary("Hello");
        
        // Update description
        res.release();
        res = client.put(base + "/MyService;atom", entry, defaultOpts);
        assertEquals(204, res.getStatus());
        
        // check to see if it's all OK
        res.release();
        res = client.get(base + "/MyService;atom", defaultOpts);
        entry = assertAndGetEntry(res, 200);
//        assertEquals("Hello", entry.getContent());
        
        ExtensibleElement metadata = getMetadata(entry);

        Element prop = factory.newElement(new QName(ItemCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test");
        prop.setAttributeValue("value", "test");
        
        prettyPrint(entry);
        // Create a new version
        res.release();
        res = client.put(base + "/MyService;atom", entry, defaultOpts);
        assertEquals(204, res.getStatus());
        
        metadata = getMetadata(entry);
        prop = getProperty(metadata, "test");
        assertNotNull(prop);
        prop.removeAttribute(new QName("value"));
        
        // Test getting the property
        res = client.get(base + "/MyService;atom", defaultOpts);
        entry = assertAndGetEntry(res, 200);
//        assertEquals("Hello", entry.getContent());

        // Remove the property
        metadata = getMetadata(entry);
        prop = getProperty(metadata, "test");
        assertNotNull(prop);
        prop.removeAttribute(new QName("value"));

        // Update and test again
        res.release();
        res = client.put(base + "/MyService;atom", entry, defaultOpts);
        assertEquals(204, res.getStatus());

        res = client.get(base + "/MyService;atom", defaultOpts);
        entry = assertAndGetEntry(res, 200);
        metadata = getMetadata(entry);
        prop = getProperty(metadata, "test");
        assertNull(prop);
    }

    private Element getProperty(ExtensibleElement metadata, String name) {
        for (Element e : metadata.getExtensions(new QName(ItemCollection.NAMESPACE, "property"))) {
            if (name.equals(e.getAttributeValue("name"))) return e;
        }
        return null;
    }

}
