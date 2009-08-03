package org.mule.galaxy.atom;


import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.test.AbstractAtomTest;

public class CommentCollectionTest extends AbstractAtomTest {
    
    public void testAddWsdl() throws Exception {
        
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String base = "http://localhost:9002/api/comments";
        
        // Grab the feed
        ClientResponse res = client.get(base, defaultOpts);
        assertEquals(res.getStatusText(), 200, res.getStatus());
        prettyPrint(res.getDocument());
        
        res.release();
    }
    
}
