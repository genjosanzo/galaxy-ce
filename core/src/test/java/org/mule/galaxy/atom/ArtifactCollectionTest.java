package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.Constants;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.spring.SpringAbderaServlet;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Registry;
import org.springmodules.jcr.SessionFactory;
import org.springmodules.jcr.support.OpenSessionInViewFilter;

public class ArtifactCollectionTest extends AbstractAtomTest {
    
    @Test
    public void testAddWsdl() throws Exception {
        assertNotNull(registry);
        
        AbderaClient client = new AbderaClient(abdera);

        String base = "http://localhost:9002/repository/";
        // Grab workspaces & collections
        ClientResponse res = client.get(base);
        assertEquals(200, res.getStatus());
        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Service> svcDoc = res.getDocument();
        Service root = svcDoc.getRoot();
        
        List<Workspace> workspaces = root.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Workspace workspace = workspaces.get(0);
        assertEquals("Galaxy Registry & Repository", workspace.getTitle());
        
        List<Collection> collections = workspace.getCollections();
        assertEquals(1, collections.size());
        
        Collection collection = collections.get(0);
        
        System.out.println(collection.getHref().toString());
        assertEquals("workspaces/Default%20Workspace", collection.getHref().toString());

        // Check out the feed, yo
        System.out.println("Grabbing the Feed");
        IRI colUri = new IRI(base).resolve(collection.getHref());
        res = client.get(Escaping.encode(colUri.toString(), Constants.PATH));
        assertEquals(200, res.getStatus());
        prettyPrint(res.getDocument());
        
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL");
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        
        res = client.post(colUri.toASCIIString(), getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        prettyPrint(res.getDocument());
        
        // Check the new feed for our entry
        System.out.println("Grabbing the Feed Again");
        res = client.get(Escaping.encode(colUri.toString(), Constants.PATH));
        assertEquals(200, res.getStatus());
        prettyPrint(res.getDocument());
        
        org.apache.abdera.model.Document<Feed> feedDoc = res.getDocument();
        Feed feed = feedDoc.getRoot();
        
        List<Entry> entries = feed.getEntries();
        assertEquals(1, entries.size());
        
        Entry e = entries.get(0);
        assertEquals("hello_world.wsdl", e.getTitle());
    }


    private InputStream getWsdl() {
        return getResourceAsStream("/wsdl/hello.wsdl");
    }
}
