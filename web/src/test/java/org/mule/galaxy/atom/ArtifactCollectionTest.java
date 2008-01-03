package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
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
        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Service> svcDoc = res.getDocument();
        Service root = svcDoc.getRoot();
        
        List<Workspace> workspaces = root.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Workspace workspace = workspaces.get(0);
        assertEquals("Mule Galaxy Registry & Repository", workspace.getTitle());
        
        List<Collection> collections = workspace.getCollections();
        
        assertEquals(1, collections.size());
        Collection collection = collections.get(0);
        
        assertEquals("registry", collection.getHref().toString());

        // Check out the feed, yo
        IRI colUri = new IRI(base).resolve(collection.getHref());
        System.out.println("Grabbing the Feed " + colUri.toString());
        res = client.get(colUri.toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        prettyPrint(res.getDocument());
        
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL");
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setHeader("X-Workspace", "Default Workspace");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        res = client.post(colUri.toASCIIString(), getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        prettyPrint(res.getDocument());
        
        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(UrlEncoding.encode(colUri.toString(), Profile.PATH.filter()), defaultOpts);
        assertEquals(200, res.getStatus());
        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        List<Entry> entries = feed.getEntries();
        assertEquals(5, entries.size());
        
        Entry e = null;
        for (Entry e2 : entries) {
            if (e2.getTitle().equals("hello_world.wsdl")) {
                e = e2;
            }
        }
        assertNotNull(e);
        
        res = client.get(e.getContentSrc().toString(), defaultOpts);
        assertEquals(200, res.getStatus());
        
        InputStream is = res.getInputStream();
        while (is.read() != -1);
        
        
    }
    
    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
