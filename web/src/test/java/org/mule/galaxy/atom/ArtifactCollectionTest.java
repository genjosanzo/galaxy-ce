package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
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

public class ArtifactCollectionTest extends AbstractAtomTest {

    public void testAddWsdl() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String base = "http://localhost:9002/api/";
        // Grab workspaces & collections
        ClientResponse res = client.get(base, defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());

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
        res.release();
        
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL " + colUri.toString());
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        String defaultWkspcCol = colUri.toString() + "/Default%20Workspace";
        res = client.post(defaultWkspcCol, getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1", 
                     res.getLocation().toString());
        res.release();
        
        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(UrlEncoding.encode(defaultWkspcCol, Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        
        org.apache.abdera.model.Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        List<Entry> entries = feed.getEntries();
        assertEquals(6, entries.size());
        
        Entry e = null;
        for (Entry e2 : entries) {
            if (e2.getTitle().equals("hello_world.wsdl")) {
                e = e2;
            }
        }
        assertNotNull(e);
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl;atom", e.getEditLink().getHref().toString());
        res.release();
        
        // Grab the feed with a "/" at the end
        res = client.get(UrlEncoding.encode(defaultWkspcCol, Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(6, entries.size());
        
        // Grab the feed for the workspace
        res = client.get(UrlEncoding.encode(defaultWkspcCol, Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(6, entries.size());
        
        // get the individual entry
        System.out.println("Getting entry " + e.getEditLinkResolvedHref().toString());
        res = client.get(e.getEditLinkResolvedHref().toString(), defaultOpts);
        org.apache.abdera.model.Document<Entry> entryDoc = res.getDocument();
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
        res = client.get("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl;history", defaultOpts);
        feedDoc = res.getDocument();
        feed = feedDoc.getRoot();
        
        entries = feed.getEntries();
        assertEquals(1, entries.size());
        
        Entry historyEntry = entries.get(0);
        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1",
                     historyEntry.getContentSrc().toString());
        res.release();
        
        // Get the raw content
        res = client.get(historyEntry.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        
        InputStream is = res.getInputStream();
        while (is.read() != -1);
        res.release();
        
        // HEAD the resource
        res = client.head(historyEntry.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        res = client.get("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        // Try the history entry
        res = client.get(historyEntry.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        
        is = res.getInputStream();
        while (is.read() != -1);
        res.release();
        
        // Add a new version
        System.out.println("Adding new version from a WSDL " + colUri.toString());
        
        opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("Slug", "0.2");
        opts.setAuthorization(defaultOpts.getAuthorization());

        // ensure we can't put
        res = client.put(colUri.toString() + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
        assertEquals(405, res.getStatus());
        res.release();

        // post is what should work instead
        res = client.post(colUri.toString() + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", res.getLocation().toString());
        res.release();
        
        // Get the entry
        String v2Uri = colUri.toString() + "/Default%20Workspace/hello_world.wsdl/0.2;atom";
        System.out.println("Getting entry " + v2Uri);
        res = client.get(v2Uri, defaultOpts);

        e = assertAndGetEntry(res, 200);
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl/0.2;atom", e.getEditLink().getHref().toString());
        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", e.getContentSrc().toString());
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", 
                     e.getLink("edit-media").getHref().toString());
        Element info = e.getExtension(new QName(ItemCollection.NAMESPACE, "item-info"));
        assertNotNull(info);
        assertEquals("0.2", info.getAttributeValue("name"));
        assertNotNull(info.getAttributeValue("created"));
        
        ExtensibleElement metadata = getMetadata(e);
        assertNotNull(metadata);

        Element lifecycleEl = metadata.getExtension(new QName(ItemCollection.NAMESPACE, "lifecycle"));
        assertNotNull(lifecycleEl);
        assertEquals("primary.lifecycle", lifecycleEl.getAttributeValue("property"));
        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
        assertEquals("Created", lifecycleEl.getAttributeValue("phase"));
        
        Element next = lifecycleEl.getFirstChild(new QName(ItemCollection.NAMESPACE, "next-phases"));
        assertNotNull(next);
        
        Element previous = lifecycleEl.getFirstChild(new QName(ItemCollection.NAMESPACE, "previous-phases"));
        assertNotNull(previous);

        Element artifactInfo = metadata.getExtension(new QName(ItemCollection.NAMESPACE, "artifact"));
        assertNotNull(artifactInfo);
        assertEquals("application/xml", artifactInfo.getAttributeValue("mediaType"));
        assertEquals("{http://schemas.xmlsoap.org/wsdl/}definitions", artifactInfo.getAttributeValue("documentType"));
        
        List<Element> properties = metadata.getExtensions(new QName(ItemCollection.NAMESPACE, "property"));
        assertTrue(properties.size() > 0);
        int size = properties.size();
        
        res.release();
        
        // update properties
        lifecycleEl.setAttributeValue("phase", "Developed");
        
        for (Element propEl : properties) {
            propEl.discard();
        }
        
        Element prop = factory.newElement(new QName(ItemCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test1");
        prop.setAttributeValue("value", "test1");
        
        prop = factory.newElement(new QName(ItemCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test2");
        Element valueEl = factory.newElement(new QName(ItemCollection.NAMESPACE, "value"), prop);
        valueEl.setText("test2");
        valueEl = factory.newElement(new QName(ItemCollection.NAMESPACE, "value"), prop);
        valueEl.setText("test2");
        
        prop = factory.newElement(new QName(ItemCollection.NAMESPACE, "property"), metadata);
        prop.setAttributeValue("name", "test3");
        prop.setAttributeValue("value", "test3");
        prop.setAttributeValue("visible", "false");

        res = client.put(v2Uri, e, defaultOpts);
        assertEquals(204, res.getStatus());
        res.release();

        // Try to show the hidden metadata
        res = client.get(v2Uri + "?showHiddenProperties=true", defaultOpts);
        e = assertAndGetEntry(res, 200);
        
        metadata = getMetadata(e);
        
        // check versioned metadata
        properties = metadata.getExtensions(new QName(ItemCollection.NAMESPACE, "property"));
        assertEquals(size + 3, properties.size());

        lifecycleEl = metadata.getExtension(new QName(ItemCollection.NAMESPACE, "lifecycle"));
        assertNotNull(lifecycleEl);
        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
        assertEquals("Developed", lifecycleEl.getAttributeValue("phase"));
        
        res.release();
        
        res = client.delete(colUri.toString() + "/Default%20Workspace/hello_world.wsdl");
        assertEquals(204, res.getStatus());
        res.release();
    }
    
    public void testVersionDelete() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String collection = "http://localhost:9002/api/registry";

        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL " + collection);
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        ClientResponse res = client.post(collection + "/Default%20Workspace", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1", 
                     res.getLocation().toString());
        res.release();
        
        res = client.delete(collection + "/Default%20Workspace/hello_world.wsdl/0.1;atom", defaultOpts);
        assertEquals(204, res.getStatus());
        res.release();
        
        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl/0.1;atom", defaultOpts);
        assertEquals(404, res.getStatus());
        res.release();

        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl/0.1", defaultOpts);
        assertEquals(404, res.getStatus());
        res.release();
        
        // create multiple versions and delete one
        res = client.post(collection + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        res.release();

        opts.setHeader("X-Artifact-Version", "0.2");
        res = client.post(collection + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        res.release();
        
        res = client.delete(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2", defaultOpts);
        assertEquals(204, res.getStatus());
        res.release();

        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2", defaultOpts);
        assertEquals(404, res.getStatus());
        res.release();
        
        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
        
        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.1", defaultOpts);
        assertEquals(200, res.getStatus());
        res.release();
    }
    
    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
