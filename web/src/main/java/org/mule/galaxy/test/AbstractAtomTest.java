package org.mule.galaxy.test;


import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.IndexManagerImpl;

import java.io.File;
import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import junit.framework.TestCase;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AbstractAtomTest extends TestCase {
    
    protected Registry registry;
    protected ServiceContext abderaServiceContext;
    protected Abdera abdera = new Abdera();
    protected Factory factory = abdera.getFactory();
    private Server server;
    private WebAppContext context;

    @Override
    protected void setUp() throws Exception {
//        System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
//                           "org.apache.xerces.jaxp.validation.XMLSchemaFactory");


        super.setUp();
        initializeJetty();
    }

    @Override
    protected void tearDown() throws Exception {
        ((IndexManagerImpl) getApplicationContext().getBean("indexManagerTarget")).destroy();
        
        clearJcrRepository();
        try {
            server.stop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        super.tearDown();
    } 
    
    private void clearJcrRepository() {
        try {
            WebApplicationContext wac = getApplicationContext();
            Repository repository = (Repository) wac.getBean("repository");
            Session session = repository.login(new SimpleCredentials("username", "password".toCharArray()));

            Node node = session.getRootNode();
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Node child = itr.nextNode();
                if (!child.getName().equals("jcr:system")) {
                    child.remove();
                }
            }
            session.save();
            session.logout();
        } catch (PathNotFoundException t) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private WebApplicationContext getApplicationContext() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context.getServletContext());
        return wac;
    }

    protected void prettyPrint(Base doc) throws IOException {
        WriterFactory writerFactory = abdera.getWriterFactory();
        Writer writer = writerFactory.getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
    }

    private void initializeJetty() throws Exception {
        server = new Server(9002); 
        
        context = new WebAppContext();
        context.setContextPath("/");

        context.setWar(getWebappDirectory());
        System.out.println("Webapp dir " + getWebappDirectory());
        server.setHandler(context);
        server.setStopAtShutdown(true);
        
        server.start();
    }

    /**
     * The webapp relative directory varies depending on run from Maven, Eclipse and IDEA
     * this will check each possiblilty to return an existing location
     * @return
     */
    protected final String getWebappDirectory() 
    {
        File f = new File("./src/main/webapp");
        if(!f.exists())
        {
            f = new File("./web/src/main/webapp");
            if(!f.exists())
            {
                f = new File("../../web/src/main/webapp");
            }
        }
        return f.getAbsolutePath();
    }
    
}
