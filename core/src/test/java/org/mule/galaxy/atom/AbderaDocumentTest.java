package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import org.mortbay.jetty.servlet.ServletHolder;
import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.Registry;
import org.mule.galaxy.util.DOMUtils;

import org.w3c.dom.Document;

public class AbderaDocumentTest extends AbstractGalaxyTest {
    protected Registry registry;
    protected ServiceContext abderaServiceContext;
    private Server server;
    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        abderaServiceContext = (ServiceContext) applicationContext.getBean(ServiceContext.class.getName());
        initializeJetty();
    }

    @Override
    protected void onTearDown() throws Exception {
        server.stop();
        
        super.onTearDown();
    }

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
        opts.setHeader("X-Version-Label", "0.1");
        
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
        
        
//        
//        IRI location = res.getLocation();
//        
//        assertTrue(location.toString().endsWith("hello_world.wsdl.atom"));
//        
//        // GET the entry
//        res = client.get(location.toString());
//        assertEquals(200, res.getStatus());
//        
//        prettyPrint(abdera, res.getDocument());
//        org.apache.abdera.model.Document<Entry> entry_doc = res.getDocument();
//        Entry entry = entry_doc.getRoot();
//        
//        IRI contentSrc = entry.getContentSrc();
//        // assertEquals(base + "documents/feed/media/0-hello_world.wsdl", contentSrc.toString());
//        
//        Link editMediaLink = entry.getLink("edit-media");
//        assertNotNull(editMediaLink);
//        
//        // Get the collection URI from the service document
//        org.apache.abdera.model.Document<Service> service_doc = client.get(base).getDocument();
//        prettyPrint(abdera, service_doc);
//        
//        Service service = service_doc.getRoot();
//        List<Workspace> workspaces = service.getWorkspaces();
//        for (Workspace w : workspaces) { 
//            System.out.println("Workspace: " + w.getTitle());
//            List<Collection> collections = w.getCollections();
//            for (Collection c : collections) {
//                System.out.println("Collection: " + c.getTitle() + " at " + c.getHref());
//            }
//            
//        }
//        // GET the media
////        IRI mediaIri = new IRI(base).resolve(contentSrc);
////        res = client.get(mediaIri.toString());
////        assertEquals(200, res.getStatus());
////        
////        byte[] doc1 =  read(res.getInputStream());
////        // for somereason these aren't exact
//////        byte[] doc2 =  read(getWsdl());
//////        assertEquals(doc1.length, doc2.length);
////        assertTrue(doc1.length > 1000);
//        
//        // Look at the versions doc:
//        System.out.println("Getting " + base + "versions/0");
//        res = client.get(base + "versions/0");
//        assertEquals(200, res.getStatus());
//        
//        prettyPrint(abdera, res.getDocument());
//        org.apache.abdera.model.Document<Feed>  feed_doc = res.getDocument();
//        Feed feed = feed_doc.getRoot();
//        
//        entry = feed.getEntries().get(0);
//        
//        //Thread.sleep(100000000);
//        //assertEquals(base + "versions/0.0.1", contentSrc.toString());
//        
//        editMediaLink = entry.getLink("edit-media");
//        assertNotNull(editMediaLink);
//        
//        assertEquals(base + "versions/0.0.1", editMediaLink.getHref().toString());
//        
//        // Delete the document
//        res = client.delete(location.toString());
//        assertEquals(204, res.getStatus());
//        
//        assertEquals(0, registry.getArtifacts().size());
    }


    private void prettyPrint(Base doc) throws IOException {
        abdera.getWriterFactory()
        .getWriter("prettyxml")
        .writeTo(doc, System.out);
        System.out.println();
    }

    private void initializeJetty() throws Exception {
        assertNotNull(abderaServiceContext);
        
        server = new Server(9002);    
        Context root = new Context(server, "/", Context.SESSIONS);
        root.addServlet(new ServletHolder(new SpringAbderaServlet() {

            @Override
            protected ServiceContext createServiceContext() {
                abderaServiceContext.init(getAbdera(), getProperties(getServletConfig()));
                return abderaServiceContext;
            }
        }), "/*");
        server.start();
    }

    private InputStream getWsdl() {
        return getResourceAsStream("/wsdl/hello.wsdl");
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-abdera.xml", 
                              "/META-INF/applicationContext-core.xml" };
        
    }

}
