package org.mule.galaxy.atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class WorkspaceCollectionTest extends AbstractAtomTest {
    
    public void testRootWorkspaceManipulation() throws Exception {
        testWorkspace("http://localhost:9002/api/registry", 1);
    }
    
    public void testWorkspaceManipulation() throws Exception {
        testWorkspace("http://localhost:9002/api/registry/Default%20Workspace", 6);
    }
    
    private void testWorkspace(String dwBase, int entries)
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
        
        // Create an Entry to represent a workspace
        Entry entry = factory.newEntry();
        entry.setTitle("MyWorkspace");
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        // Once we support workspace descriptions, the description will go here
        entry.setContent("");
        
        Element wInfo = factory.newElement(new QName(AbstractItemCollection.NAMESPACE, "workspace-info"));
        wInfo.setAttributeValue("name", "MyWorkspace");
        entry.addExtension(wInfo);
        
        res = client.post(dwBase, entry, defaultOpts);
        assertEquals(201, res.getStatus());
        Document<Entry> entryDoc = res.getDocument();
        prettyPrint(entryDoc);
        entry = entryDoc.getRoot();
        assertEquals("MyWorkspace", entry.getTitle());
        
        Collection itemsResponse = null;
        for (Element e : entry.getExtensions()) {
            if (e instanceof Collection) {
                itemsResponse = (Collection) e;
            }
        }
        assertNotNull(itemsResponse);
        
        wInfo = entry.getExtension(new QName(AbstractItemCollection.NAMESPACE, "workspace-info"));
        assertNotNull(wInfo);
        res.release();
        
        // Add an entry to the new workspace
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        res = client.post(dwBase + "/MyWorkspace", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        // TODO: test the entry's links. They aren't quite right yet.
        
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
    public void testRootWorkspaceManipulationDeprecated() throws Exception {
        testWorkspaceDeprecated("http://localhost:9002/api/registry", 1);
    }
    
    public void testWorkspaceManipulationDeprecated() throws Exception {
        testWorkspaceDeprecated("http://localhost:9002/api/registry/Default%20Workspace", 0);
    }

    private void testWorkspaceDeprecated(String dwBase, int entries)
        throws IOException {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        // Grab workspaces & collections
        ClientResponse res = client.get(dwBase + ";workspaces", defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());
        
        Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        assertEquals(entries, feed.getEntries().size());
        
        Entry entry = factory.newEntry();
        entry.setTitle("MyWorkspace");
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        // Once we support workspace descriptions, the description will go here
        entry.setContent("");
        res.release();
        
        res = client.post(dwBase + ";workspaces", entry, defaultOpts);
        assertEquals(201, res.getStatus());
        Document<Entry> entryDoc = res.getDocument();
//        prettyPrint(entryDoc);
        entry = entryDoc.getRoot();
        assertEquals("MyWorkspace", entry.getTitle());
        
        res.release();
        
        // Add an entry to the new workspace
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        res = client.post(dwBase + "/MyWorkspace", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        res.release();
        
        // TODO: test the entry's links. They aren't quite right yet.
        
        // Grab the new workspace's feed
        res = client.get(dwBase + "/MyWorkspace", defaultOpts);
        feedDoc = res.getDocument();
        prettyPrint(feedDoc);
        feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
        res.release();
        
        res = client.get(dwBase + "/MyWorkspace", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // delete the workspace
        res = client.delete(dwBase + "/MyWorkspace", defaultOpts);
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
        Entry entry = factory.newEntry();
        entry.setTitle("Default Workspace");
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        // Once we support workspace descriptions, the description will go here
        entry.setContent("");
        
        ClientResponse res = client.post("http://localhost:9002/api/registry;workspaces", entry, defaultOpts);
        
        assertEquals(409, res.getStatus());
        
        res.release();
    }
    

    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
