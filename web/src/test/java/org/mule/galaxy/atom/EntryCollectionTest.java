package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.Date;
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

public class EntryCollectionTest extends AbstractAtomTest {

    public void testAddEntry() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        
        String base = "http://localhost:9002/api/registry/Default%20Workspace";
        
        // Create an entry
        System.out.println("Creating Entry for " + base);

        Entry entry = factory.newEntry();
        entry.setTitle("MyService");
        entry.setUpdated(new Date());
        entry.addAuthor("Dan Diephouse");
        entry.setId(factory.newUuidUri());
        entry.setContent("");
        
        Element versionEl = factory.newExtensionElement(new QName(AbstractEntryCollection.NAMESPACE, "version"), entry);
        versionEl.setAttributeValue("label", "1.0");
        
        ClientResponse res = client.post(base, entry, defaultOpts);
        assertEquals(201, res.getStatus());
//        prettyPrint(res.getDocument());
        res.release();

        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(base, defaultOpts);
        assertEquals(200, res.getStatus());
//        prettyPrint(res.getDocument());
        
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
//        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl;atom", e.getEditLink().getHref().toString());
//        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl", e.getContentSrc().toString());
//        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl", 
//                     e.getLink("edit-media").getHref().toString());
//        res.release();
//        
//        // Grab the feed with a "/" at the end
//        res = client.get(UrlEncoding.encode(colUri.toString() + "/", Profile.PATH.filter()), defaultOpts);
//        assertEquals(200, res.getStatus());
//        feedDoc = res.getDocument();
//        feed = feedDoc.getRoot();
//        
//        entries = feed.getEntries();
//        assertEquals(7, entries.size());
//        
//        // Grab the feed for the workspace
//        res = client.get(UrlEncoding.encode(colUri.toString() + "/Default%20Workspace", Profile.PATH.filter()), defaultOpts);
//        assertEquals(200, res.getStatus());
//        feedDoc = res.getDocument();
//        feed = feedDoc.getRoot();
//        
//        entries = feed.getEntries();
//        assertEquals(7, entries.size());
//        
//        // get the individual entry
//        System.out.println("Getting entry " + e.getEditLinkResolvedHref().toString());
//        res = client.get(e.getEditLinkResolvedHref().toString(), defaultOpts);
//        org.apache.abdera.model.Document<Entry> entryDoc = res.getDocument();
//        prettyPrint(entryDoc);
//        Entry entry = entryDoc.getRoot();
//        
//        Collection versionCollection = null;
//        List<Element> elements = entry.getElements();
//        for (Element el : elements) {
//            if (el instanceof Collection) {
//                versionCollection = (Collection) el;
//            }
//        }
//        assertNotNull(versionCollection);
//        res.release();
//        
//        // try getting the version history
//        res = client.get(e.getContentSrc().toString() + ";history", defaultOpts);
////        prettyPrint(res.getDocument());
//        feedDoc = res.getDocument();
//        feed = feedDoc.getRoot();
//        
//        entries = feed.getEntries();
//        assertEquals(1, entries.size());
//        
//        Entry historyEntry = entries.get(0);
//        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.1",
//                     historyEntry.getContentSrc().toString());
//        res.release();
//        
//        // Get the raw content
//        res = client.get(e.getContentSrc().toString(), defaultOpts);
//        assertEquals(200, res.getStatus());
//        
//        InputStream is = res.getInputStream();
//        while (is.read() != -1);
//        res.release();
//        
//        // HEAD the resource
//        res = client.head(e.getContentSrc().toString(), defaultOpts);
//        assertEquals(200, res.getStatus());
//        res.release();
//        
//        res = client.get(e.getContentSrc().toString() + "?version=0.1", defaultOpts);
//        assertEquals(200, res.getStatus());
//        res.release();
//        
//        // Try the history entry
//        res = client.get(historyEntry.getContentSrc().toString(), defaultOpts);
//        assertEquals(200, res.getStatus());
//        
//        is = res.getInputStream();
//        while (is.read() != -1);
//        res.release();
//        
//        // Add a new version
//        System.out.println("Adding Entry from a WSDL " + colUri.toString());
//        
//        opts = new RequestOptions();
//        opts.setContentType("application/xml; charset=utf-8");
//        opts.setSlug("hello_world.wsdl");
//        opts.setHeader("X-Artifact-Version", "0.2");
//        opts.setAuthorization(defaultOpts.getAuthorization());
//        
//        res = client.put(colUri.toString() + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
//        assertEquals(200, res.getStatus());
//        res.release();
//        
//        // Get the entry
//        String v2Uri = colUri.toString() + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2";
//        System.out.println("Getting entry " + v2Uri);
//        res = client.get(v2Uri, defaultOpts);
//        
//        entryDoc = res.getDocument();
//        prettyPrint(entryDoc);
//        e = entryDoc.getRoot();
//        
//        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl;atom?version=0.2", e.getEditLink().getHref().toString());
//        assertEquals("http://localhost:9002/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", e.getContentSrc().toString());
//        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl?version=0.2", 
//                     e.getLink("edit-media").getHref().toString());
//       
//        Element info = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "artifact-info"));
//        assertNotNull(info);
//        assertEquals("application/xml", info.getAttributeValue("mediaType"));
//        assertEquals("{http://schemas.xmlsoap.org/wsdl/}definitions", info.getAttributeValue("documentType"));
//        
//        Element lifecycleEl = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "lifecycle"));
//        assertNotNull(lifecycleEl);
//        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
//        assertEquals("Created", lifecycleEl.getAttributeValue("phase"));
//        
//        Element next = lifecycleEl.getFirstChild(new QName(AbstractEntryCollection.NAMESPACE, "next-phases"));
//        assertNotNull(next);
//        
//        Element previous = lifecycleEl.getFirstChild(new QName(AbstractEntryCollection.NAMESPACE, "previous-phases"));
//        assertNotNull(previous);
//        
//        Element versionEl = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "version"));
//        assertNotNull(lifecycleEl);
//        assertEquals("0.2", versionEl.getAttributeValue("label"));
//        assertEquals("true", versionEl.getAttributeValue("default"));
//        assertEquals("true", versionEl.getAttributeValue("enabled"));
//        
//        ExtensibleElement metadata = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "metadata"));
//        List<Element> properties = metadata.getExtensions(new QName(AbstractEntryCollection.NAMESPACE, "property"));
//        assertTrue(properties.size() > 0);
//        int size = properties.size();
//        
//        res.release();
//        
//        // update metadata/lifecycle/enabled/default
//        lifecycleEl.setAttributeValue("phase", "Developed");
//        
//        for (Element propEl : properties) {
//            propEl.discard();
//        }
//        
//        Element prop = factory.newElement(new QName(AbstractEntryCollection.NAMESPACE, "property"), metadata);
//        prop.setAttributeValue("name", "test1");
//        prop.setAttributeValue("value", "test1");
//        
//        prop = factory.newElement(new QName(AbstractEntryCollection.NAMESPACE, "property"), metadata);
//        prop.setAttributeValue("name", "test2");
//        Element valueEl = factory.newElement(new QName(AbstractEntryCollection.NAMESPACE, "value"), prop);
//        valueEl.setText("test2");
//        valueEl = factory.newElement(new QName(AbstractEntryCollection.NAMESPACE, "value"), prop);
//        valueEl.setText("test2");
//        
//        prop = factory.newElement(new QName(AbstractEntryCollection.NAMESPACE, "property"), metadata);
//        prop.setAttributeValue("name", "test3");
//        prop.setAttributeValue("value", "test3");
//        prop.setAttributeValue("visible", "false");
//        
//        // GALAXY-162 no way to change version labels now
////        versionEl.setAttributeValue("label", "3.0");
//        versionEl.setAttributeValue("enabled", "false");
//        
//        res = client.put(v2Uri, e, defaultOpts);
//        assertEquals(204, res.getStatus());
//        res.release();
//        
//        // Try to show the hidden metadata
//        res = client.get(v2Uri + "&showHiddenMetadata=true", defaultOpts);
//        
//        entryDoc = res.getDocument();
//        prettyPrint(entryDoc);
//        e = entryDoc.getRoot();
//        
//        metadata = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "metadata"));
//        properties = metadata.getExtensions(new QName(AbstractEntryCollection.NAMESPACE, "property"));
//        assertEquals(size + 2, properties.size());
//        
//        res.release();
//        
//        // check the metadata
//        System.out.println("Getting entry again " + v2Uri);
//        res = client.get(v2Uri, defaultOpts);
//
//        entryDoc = res.getDocument();
//        prettyPrint(entryDoc);
//        e = entryDoc.getRoot();
//        
//        lifecycleEl = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "lifecycle"));
//        assertNotNull(lifecycleEl);
//        assertEquals("Default", lifecycleEl.getAttributeValue("name"));
//        assertEquals("Developed", lifecycleEl.getAttributeValue("phase"));
//        
//        versionEl = e.getExtension(new QName(AbstractEntryCollection.NAMESPACE, "version"));
//        assertNotNull(lifecycleEl);
////        assertEquals("3.0", versionEl.getAttributeValue("label"));
//        
//        assertEquals("false", versionEl.getAttributeValue("enabled"));
//        
//        res.release();
//        
//        res = client.delete(colUri.toString() + "/Default%20Workspace/hello_world.wsdl");
//        assertEquals(204, res.getStatus());
//        res.release();
//    }
//    
//    public void testVersionDelete() throws Exception {
//        AbderaClient client = new AbderaClient(abdera);
//        RequestOptions defaultOpts = client.getDefaultRequestOptions();
//        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
//        
//        String collection = "http://localhost:9002/api/registry";
//
//        // Testing of entry creation
//        System.out.println("Creating Entry from a WSDL " + collection);
//        
//        RequestOptions opts = new RequestOptions();
//        opts.setContentType("application/xml; charset=utf-8");
//        opts.setSlug("hello_world.wsdl");
//        opts.setHeader("X-Artifact-Version", "0.1");
//        opts.setAuthorization(defaultOpts.getAuthorization());
//        
//        ClientResponse res = client.post(collection + "/Default%20Workspace", getWsdl(), opts);
//        assertEquals(201, res.getStatus());
//        
//        assertEquals("/api/registry/Default%20Workspace/hello_world.wsdl", 
//                     res.getLocation().toString());
//        res.release();
//        
//        res = client.delete(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.1", defaultOpts);
//        assertEquals(204, res.getStatus());
//        res.release();
//        
//        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.1", defaultOpts);
//        assertEquals(404, res.getStatus());
//        res.release();
//        
//        // create multiple versions and delete one
//        res = client.post(collection + "/Default%20Workspace", getWsdl(), opts);
//        assertEquals(201, res.getStatus());
//        res.release();
//
//        opts.setHeader("X-Artifact-Version", "0.2");
//        res = client.put(collection + "/Default%20Workspace/hello_world.wsdl", getWsdl(), opts);
//        assertEquals(200, res.getStatus());
//        res.release();
//        
//        res = client.delete(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2", defaultOpts);
//        assertEquals(204, res.getStatus());
//        res.release();
//
//        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.2", defaultOpts);
//        assertEquals(404, res.getStatus());
//        res.release();
//        
//        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom", defaultOpts);
//        assertEquals(200, res.getStatus());
//        res.release();
//        
//        res = client.get(collection + "/Default%20Workspace/hello_world.wsdl;atom?version=0.1", defaultOpts);
//        assertEquals(200, res.getStatus());
//        res.release();
    }
    
    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
