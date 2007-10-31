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
import org.apache.abdera.protocol.util.EncodingUtil;
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

public class ArtifactCollectionQueryTest extends AbstractAtomTest {
    
    @Test
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
        
        String search = EncodingUtil.encode("select artifact where artifactVersion.wsdl.service = 'HelloWorldService'");
        url = url + "?q=" + search;
        
        res = client.get(url);
        
        prettyPrint(res.getDocument());
    }


    private InputStream getWsdl() {
        return getResourceAsStream("/wsdl/hello.wsdl");
    }
}
