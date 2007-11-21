package org.mule.galaxy.atom;


import java.io.InputStream;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.util.EncodingUtil;

public class ArtifactCollectionQueryTest extends AbstractAtomTest {
    
    public void testAddWsdl() throws Exception {
        assertNotNull(registry);
        
        AbderaClient client = new AbderaClient(abdera);

        String url = "http://localhost:9002/repository/workspaces/Default%20Workspace";
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL");
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        
        ClientResponse res = client.post(url, getWsdl(), opts);
        assertEquals(201, res.getStatus());
        
        prettyPrint(res.getDocument());
        
        opts = new RequestOptions();
        
        String search = EncodingUtil.encode("select artifactVersion where artifactVersion.wsdl.service = 'HelloWorldService'");
        url = url + "?q=" + search;
        
        res = client.get(url);
        
        prettyPrint(res.getDocument());
    }


    private InputStream getWsdl() {
        return getResourceAsStream("/wsdl/hello.wsdl");
    }
}
