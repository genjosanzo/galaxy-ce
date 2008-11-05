package org.mule.galaxy.example.atompub;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class AtomPubExampleTest extends AbstractAtomTest {
    public void testExamples() throws Exception {
        AbderaClient client = new AbderaClient();

        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        defaultOpts.setContentType("application/atom+xml;type=entry");

        // Create a "Services" workspace
        ClientResponse result = client.post("http://localhost:9002/api/registry",
                                            getResourceAsStream("/add-workspace.xml"),
                                            defaultOpts);
        assertEquals(201, result.getStatus());
        prettyPrint(result.getDocument());
        
        result = client.get("http://localhost:9002/api/registry", defaultOpts);
    }
}