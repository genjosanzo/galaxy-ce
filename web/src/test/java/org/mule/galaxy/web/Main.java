package org.mule.galaxy.web;


import java.util.HashMap;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mule.galaxy.util.IOUtils;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Server server = new Server();    
        Connector connector = new SocketConnector();
        connector.setPort(9002);
        connector.setHost("127.0.0.1");
        server.addConnector(connector);

        
        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
//        wac.setWar("./target/galaxy-web-1.0-M1-SNAPSHOT");
        wac.setWar("./src/main/webapp");
        
        HashMap params = new HashMap();
        params.put("useFileMappedBuffer", Boolean.FALSE);
        wac.setInitParams(params);
        
        server.addHandler(wac);
        
        server.setStopAtShutdown(true);

        server.start();
        
        addDocuments();
    }

    private static void addDocuments() {
        AbderaClient client = new AbderaClient();
        
        addArtifact(client, "hello-config.xml", "/mule/hello-config.xml");
        addArtifact(client, "hello.xsd", "/wsdl/imports/hello.xsd");
        addArtifact(client, "hello-portType.wsdl", "/wsdl/imports/hello-portType.wsdl");
        addArtifact(client, "hello.wsdl", "/wsdl/imports/hello.wsdl");   
    }

    private static void addArtifact(AbderaClient client, String name, String resource) {
        // POST a Mule configuration
        String url = "http://localhost:9002/api/repository";
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug(name);
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setHeader("X-Workspace", "Default Workspace");
        opts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        ClientResponse res = client.post(url, Main.class.getResourceAsStream(resource), opts);
    }

}
