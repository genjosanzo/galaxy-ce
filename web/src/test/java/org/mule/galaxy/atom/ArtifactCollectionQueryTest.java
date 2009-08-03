package org.mule.galaxy.atom;


import java.io.InputStream;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class ArtifactCollectionQueryTest extends AbstractAtomTest {
    
    public void testAddWsdl() throws Exception {
        AbderaClient client = new AbderaClient(abdera);

        String url = "http://localhost:9002/api/registry";
        // Testing of entry creation
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        ClientResponse res = client.post(url + "/Default%20Workspace", getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        prettyPrint(res.getDocument());
        
        opts = new RequestOptions();
        
        String search = UrlEncoding.encode("select where wsdl.service = 'HelloWorldService'");
        url = url + "?q=" + search;
        
        RequestOptions opts2 = client.getDefaultRequestOptions();
        opts2.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        res = client.get(url, opts2);
        
        prettyPrint(res.getDocument());
    }

    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/hello.wsdl");
    }
}
