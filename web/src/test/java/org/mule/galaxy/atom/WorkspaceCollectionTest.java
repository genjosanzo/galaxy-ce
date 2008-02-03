package org.mule.galaxy.atom;

import java.io.InputStream;
import java.util.Date;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.galaxy.util.IOUtils;

public class WorkspaceCollectionTest extends AbstractAtomTest {
    
    public void testWorkspaceManipulation() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        String base = "http://localhost:9002/api/registry";
        String dwBase = base + "/Default%20Workspace";
        // Grab workspaces & collections
        ClientResponse res = client.get(dwBase + ";workspaces", defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());
        
        Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        assertEquals(0, feed.getEntries().size());
        
        Entry entry = factory.newEntry();
        entry.setTitle("MyWorkspace");
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        // Once we support workspace descriptions, the description will go here
        entry.setContent("");
        
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
        
        // TODO: test the entry's links. They aren't quite right yet.
        
        // Grab the new workspace's feed
        res = client.get(dwBase + "/MyWorkspace");
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        assertEquals(1, feed.getEntries().size());
        
        // delete the workspace
        res = client.delete(dwBase + "/MyWorkspace", defaultOpts);
        assertEquals(204, res.getStatus());
        
        res.release();
        
        res = client.get(base + ";workspaces");
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        prettyPrint(feed);
        assertEquals(1, feed.getEntries().size());
    }

    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
