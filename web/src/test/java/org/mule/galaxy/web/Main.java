package org.mule.galaxy.web;


import java.util.HashMap;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Server server = new Server();    
        Connector connector = new SocketConnector();
        connector.setPort(9002);
        connector.setHost("127.0.0.1");
        server.addConnector(connector);

        
        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
        wac.setWar("./src/main/webapp");    // this is path to .war OR TO expanded, existing webapp; WILL FIND web.xml and parse it
        
        HashMap params = new HashMap();
        params.put("useFileMappedBuffer", Boolean.FALSE);
        wac.setInitParams(params);
        
        server.addHandler(wac);
        
        server.setStopAtShutdown(true);

        server.start();
    }

}
