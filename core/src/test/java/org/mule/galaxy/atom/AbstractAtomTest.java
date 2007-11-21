package org.mule.galaxy.atom;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.spring.SpringAbderaServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Registry;
import org.springmodules.jcr.SessionFactory;
import org.springmodules.jcr.support.OpenSessionInViewFilter;

public class AbstractAtomTest extends AbstractGalaxyTest {
    
    protected Registry registry;
    protected ServiceContext abderaServiceContext;
    private Server server;
    Abdera abdera = new Abdera();
    Factory factory = abdera.getFactory();
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        abderaServiceContext = (ServiceContext) applicationContext.getBean(ServiceContext.class.getName());
        
        initializeJetty();
    }

    @Override
    protected void onTearDown() throws Exception {
        server.stop();
        
        super.onTearDown();
    }

    protected void prettyPrint(Base doc) throws IOException {
        abdera.getWriterFactory()
        .getWriter("prettyxml")
        .writeTo(doc, System.out);
        System.out.println();
    }

    private void initializeJetty() throws Exception {
        assertNotNull(abderaServiceContext);
        
        server = new Server(9002);    
        Context root = new Context(server, "/", Context.SESSIONS);
        ServletHolder holder = new ServletHolder(new SpringAbderaServlet() {

            @Override
            protected ServiceContext createServiceContext() {
                abderaServiceContext.init(getAbdera(), getProperties(getServletConfig()));
                return abderaServiceContext;
            }
        });
        
        root.addServlet(holder, "/*");
        root.addFilter(new FilterHolder(new OpenSessionInViewFilter() {

            @Override
            protected SessionFactory lookupSessionFactory(HttpServletRequest request) {
                return (SessionFactory) applicationContext.getBean("sessionFactory");
            }
            
        }), "/repository/*", 0);
        
        
        server.start();
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-abdera.xml", 
                              "/META-INF/applicationContext-core.xml" };
        
    }
    
}
