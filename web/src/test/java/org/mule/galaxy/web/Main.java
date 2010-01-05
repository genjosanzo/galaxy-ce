package org.mule.galaxy.web;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.FileResource;
import org.mortbay.resource.Resource;

public class Main {

    public static void main(String[] args) throws Exception {
        
        System.setProperty("hostedMode", "true");
        
        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(9002);
        connector.setHost("0.0.0.0");
        server.addConnector(connector);

        File base;
        if (args.length > 0) {
            base = new File(args[0]);
        } else {
            base = new File(".");
        }
        
        final File baseWebapp = new File(base, "src/main/webapp");

        final FileResource webappResource = new FileResource(baseWebapp.toURI().toURL());
        final FileResource generated = new FileResource(new File("target/gwt-webapp").toURI().toURL());
        final List<Resource> other = Arrays.asList(webappResource, (Resource) generated);
        
        WebAppContext wac = new WebAppContext() {

            @Override
            public String getDescriptor() {
                // prefer webapp
                try {
                    final File file = new File(baseWebapp, "WEB-INF/web.xml");
                    final String path = file.getCanonicalPath();
                    if (!file.exists()) {
                        throw new RuntimeException("web.xml doesn't exist: " + path);
                    }
                    return path;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Resource getResource(String uriInContext) throws MalformedURLException {
                return DefaultServlet.getResource(super.getResource(uriInContext),
                                                  other,
                                                  uriInContext);
            }

            @Override
            public String getResourceAlias(String alias) {
                return super.getResourceAlias(alias);
            }
            
        };
        wac.setContextPath("/");
        wac.setDefaultsDescriptor(new File(base, "src/test/resources/webdefault.xml").getAbsolutePath());
        wac.setWar(baseWebapp.getAbsolutePath());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("useFileMappedBuffer", Boolean.FALSE);
        wac.setInitParams(params);
        
        server.addHandler(wac);
        
        server.setStopAtShutdown(true);

        server.start();
    }

}
