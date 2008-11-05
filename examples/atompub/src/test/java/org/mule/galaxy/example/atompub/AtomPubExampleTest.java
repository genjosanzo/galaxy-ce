package org.mule.galaxy.example.atompub;

import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.atom.AbstractItemCollection;
import org.mule.galaxy.test.AbstractAtomTest;

public class AtomPubExampleTest extends AbstractAtomTest {
    public void testExamples() throws Exception {
        // START SNIPPET: setup
        AbderaClient client = new AbderaClient();

        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");
        // END SNIPPET: setup
        
        // START SNIPPET: createworkspace
        // Create a "Services" workspace
        Entry entry = factory.newEntry();
        entry.setTitle("Services");
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        entry.setContent("");
        
        // Create a <workspace-info> element
        Element wInfo = factory.newElement(new QName(AbstractItemCollection.NAMESPACE, "workspace-info"));
        wInfo.setAttributeValue("name", "Services");
        entry.addExtension(wInfo);
        
        ClientResponse result = client.post("http://localhost:9002/api/registry", entry, defaultOpts);
        // END SNIPPET: createworkspace
        assertEquals(201, result.getStatus());
        
        // START SNIPPET: addwsdl
        // Store a WSDL inside the Services workspace
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello.wsdl");
        opts.setHeader("X-Artifact-Version", "1.0");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        result = client.post("http://localhost:9002/api/registry/Services", 
                             getClass().getResourceAsStream("/hello.wsdl"), 
                             opts);
        // END SNIPPET: addwsdl
        
    }
}