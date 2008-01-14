package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
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
