package org.mule.galaxy.web;


import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        connector.setHost("0.0.0.0");
        server.addConnector(connector);

        
        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
        wac.setDefaultsDescriptor("webdefault.xml");
//        wac.setWar("./target/galaxy-web-1.0-beta-2-SNAPSHOT");
        wac.setWar("./src/main/webapp");
        
        Map params = new HashMap();
        params.put("useFileMappedBuffer", Boolean.FALSE);
        wac.setInitParams(params);
        
        server.addHandler(wac);
        
        server.setStopAtShutdown(true);

        server.start();
    }

}
