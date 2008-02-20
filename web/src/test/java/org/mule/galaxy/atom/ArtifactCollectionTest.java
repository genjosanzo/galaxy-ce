package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.galaxy.util.IOUtils;

public class ArtifactCollectionTest extends AbstractAtomTest {
    
    public void testAddWsdl() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String base = "http://localhost:9002/api/";
        // Grab workspaces & collections
        ClientResponse res = client.get(base, defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());
//        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Service> svcDoc = res.getDocument();
        Service root = svcDoc.getRoot();
        
        List<Workspace> workspaces = root.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Workspace workspace = workspaces.get(0);
        assertEquals("Mule Galaxy Registry & Repository", workspace.getTitle());
        
        List<Collection> collections = workspace.getCollections();
        
        assertEquals(2, collections.size());
        
        Collection collection = null;
        for (Collection c : collections) {
            if ("/api/registry".equals(c.getHref().toString())) {
                collection = c;
            }
        }
        
        assertNotNull(collection);
        res.release();
        
        // Check out the feed, yo
        IRI colUri = new IRI(base).resolve(collection.getHref());
        System.out.println("Grabbing the Feed " + colUri.toString());
        res = client.get(colUri.toString(), defaultOpts);
        System.out.println(res.getStatusText());
        assertEquals(200, res.getStatus());
//        prettyPrint(res.getDocument());
        res.release();
        
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL " + colUri.toString());
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        res = client.post(colUri.toString() + "/Default%20Workspace", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl", 
                     res.getLocation().toString());
        res.release();
        
        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(UrlEncoding.encode(colUri.toString(), Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
//        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        List<Entry> entries = feed.getEntries();
        assertEquals(7, entries.size());
        
        Entry e = null;
        for (Entry e2 : entries) {
            if (e2.getTitle().equals("hello_world.wsdl")) {
                e = e2;
            }
        }
        assertNotNull(e);
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl;atom", e.getEditLink().getHref().toString());
        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl", e.getContentSrc().toString());
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl", 
                     e.getLink("edit-media").getHref().toString());
        res.release();
        
        // Grab the feed with a "/" at the end
        res = client.get(UrlEncoding.encode(colUri.toString() + "/", Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(7, entries.size());
        
        // Grab the feed for the workspace
        res = client.get(UrlEncoding.encode(colUri.toString() + "/Default%20Workspace", Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(7, entries.size());
        
        // get the individual entry
        System.out.println("Getting entry " + e.getEditLinkResolvedHref().toString());
        res = client.get(e.getEditLinkResolvedHref().toString(), defaultOpts);
        org.apache.abdera.model.Document<Entry> entryDoc = res.getDocument();
        prettyPrint(entryDoc);
        Entry entry = entryDoc.getRoot();
        
        Collection versionCollection = null;
        List<Element> elements = entry.getElements();
        for (Element el : elements) {
            if (el instanceof Collection) {
                versionCollection = (Collection) el;
            }
        }
        assertNotNull(versionCollection);
        res.release();
        
        // try getting the version history
        res = client.get(e.getContentSrc().toString() + ";history", defaultOpts);
//        prettyPrint(res.getDocument());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(1, entries.size());
        
        Entry historyEntry = entries.get(0);
        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1",
                     historyEntry.getContentSrc().toString());
        res.release();
        
        // Get the raw content
        res = client.get(e.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        
        InputStream is = res.getInputStream();
        while (is.read() != -1);
        res.release();
        
        // HEAD the resource
        res = client.head(e.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        res = client.get(e.getContentSrc().toString() + "?version=0.1", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // Try the history entry
        res = client.get(historyEntry.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        
        is = res.getInputStream();
        while (is.read() != -1);
        res.release();
        
        // Add a new version
        System.out.println("Adding Entry from a WSDL " + colUri.toString());
        
        opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.2");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        res = client.put(colUri.toString() + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // Get the entry
        String v2Uri = colUri.toString() + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2";
        System.out.println("Getting entry " + v2Uri);
        res = client.get(v2Uri, defaultOpts);
        
        entryDoc = res.getDocument();
        prettyPrint(entryDoc);
        e = entryDoc.getRoot();
        
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl;atom?version=0.2", e.getEditLink().getHref().toString());
        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", e.getContentSrc().toString());
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", 
                     e.getLink("edit-media").getHref().toString());
        
        Element lifecycleEl = e.getExtension(new QName(AbstractArtifactCollection.NAMESPACE, "lifecycle"));
        assertNotNull(lifecycleEl);
        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
        assertEquals("Created", lifecycleEl.getAttributeValue("phase"));
        
        ExtensibleElement metadata = e.getExtension(new QName(AbstractArtifactCollection.NAMESPACE, "metadata"));
        List<Element> properties = metadata.getExtensions(new QName(AbstractArtifactCollection.NAMESPACE, "property"));
        assertEquals(5, properties.size());
        
        res.release();
        
        // update metadata/lifecycle
        lifecycleEl.setAttributeValue("phase", "Developed");
        
        for (Element propEl : properties) {
            propEl.discard();
        }
        
        Element prop = factory.newElement(new QName(AbstractArtifactCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test1");
        prop.setAttributeValue("value", "test1");
        
        prop = factory.newElement(new QName(AbstractArtifactCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test2");
        Element valueEl = factory.newElement(new QName(AbstractArtifactCollection.NAMESPACE, "value"), prop);
        valueEl.setText("test2");
        valueEl = factory.newElement(new QName(AbstractArtifactCollection.NAMESPACE, "value"), prop);
        valueEl.setText("test2");
        
        prettyPrint(e);
        res = client.put(v2Uri, e, defaultOpts);
        assertEquals(204, res.getStatus());
        res.release();
        
        // check the metadata
        System.out.println("Getting entry again " + v2Uri);
        res = client.get(v2Uri, defaultOpts);

        entryDoc = res.getDocument();
        prettyPrint(entryDoc);
        e = entryDoc.getRoot();
        
        lifecycleEl = e.getExtension(new QName(AbstractArtifactCollection.NAMESPACE, "lifecycle"));
        assertNotNull(lifecycleEl);
        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
        assertEquals("Developed", lifecycleEl.getAttributeValue("phase"));
        
        res.release();
        
        res = client.delete(colUri.toString() + "/Default%20Workspace/hello_world.wsdl");
        assertEquals(204, res.getStatus());
        res.release();
    }
    
    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
