package org.mule.galaxy.atom;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class AbderaMain {
    public static void main(String[] args) throws Exception {
        Server server = new org.mortbay.jetty.Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(9002);
        server.setConnectors(new Connector[] {connector});

        WebAppContext webappcontext = new WebAppContext();
        webappcontext.setContextPath("/");

        webappcontext.setWar(new File("src/main/webapp").getAbsolutePath());

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {webappcontext, new DefaultHandler()});

        server.setHandler(handlers);
        try {
            server.start();
                       
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
