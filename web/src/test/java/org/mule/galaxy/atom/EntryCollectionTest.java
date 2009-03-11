package org.mule.galaxy.atom;


import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class EntryCollectionTest extends AbstractAtomTest {

    public void testAddEntry() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        String base = "http://localhost:9002/api/registry/Default%20Workspace";
        
        // Create an entry
        System.out.println("Creating Entry for " + base);

        Entry entry = createNewEntry("1.0");
        prettyPrint(entry);
        ClientResponse res = client.post(base, entry, defaultOpts);
        assertEquals(201, res.getStatus());
//        prettyPrint(res.getDocument());
        res.release();

        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(base, defaultOpts);
        assertEquals(200, res.getStatus());

        org.apache.abdera.model.Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        List<Entry> entries = feed.getEntries();
        assertEquals(7, entries.size());
        
        Entry e = null;
        for (Entry e2 : entries) {
            if (e2.getTitle().equals("MyService")) {
                e = e2;
            }
        }
        
        assertNotNull(e);
        assertEquals("/api/registry/Default%20Workspace/MyService;atom", e.getEditLink().getHref().toString());
        res.release();
        
        res = client.get(base + "/MyService;atom", defaultOpts);
        assertEquals(200, res.getStatus());
        org.apache.abdera.model.Document<Entry> entryDoc = res.getDocument();
        entry = entryDoc.getRoot();

        entry.setContent("Hello");
        
        // Update description
        res = client.put(base + "/MyService;atom", entry, defaultOpts);
        assertEquals(204, res.getStatus());
        
        // check to see if it's all OK
        res = client.get(base + "/MyService;atom", defaultOpts);
        assertEquals(200, res.getStatus());
        entryDoc = res.getDocument();
        entry = entryDoc.getRoot();
        prettyPrint(entry);
        assertEquals("Hello", entry.getContent());
        
        // Create a new version
        entry = createNewEntry("2.0");
        res = client.post(base + "/MyService;history", entry, defaultOpts);
        assertEquals(201, res.getStatus());
        
        // Ensure it's in the history feed
        res = client.get(base + "/MyService;history", defaultOpts);
        assertEquals(200, res.getStatus());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        entries = feed.getEntries();
        assertEquals(2, entries.size());
    }

    private Entry createNewEntry(String version) {
        Entry entry = factory.newEntry();
        entry.setTitle("MyService");
        entry.setUpdated(new Date());
        entry.addAuthor("Dan Diephouse");
        entry.setId(factory.newUuidUri());
        entry.setContent("");
        
        Element versionEl = factory.newExtensionElement(new QName(AbstractItemCollection.NAMESPACE, "version"), entry);
        versionEl.setAttributeValue("label", version);
        return entry;
    }
}
