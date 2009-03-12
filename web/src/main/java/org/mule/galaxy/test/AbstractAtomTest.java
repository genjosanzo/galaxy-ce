/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.xml.namespace.QName;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mule.galaxy.Registry;
import org.mule.galaxy.atom.AbstractItemCollection;
import org.mule.galaxy.impl.index.IndexManagerImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springmodules.jcr.SessionFactory;

import junit.framework.TestCase;

public abstract class AbstractAtomTest extends TestCase {
    
    protected Registry registry;
    protected Provider provider;
    protected Abdera abdera = new Abdera();
    protected Factory factory = abdera.getFactory();
    private Server server;
    private WebAppContext context;
    protected SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        System.setProperty("galaxy.data", "./target/galaxy-data");
        super.setUp();
        initializeJetty();
        
        registry = (Registry) getApplicationContext().getBean("registry");
        sessionFactory = (SessionFactory) getApplicationContext().getBean("sessionFactory");
    }

    protected InputStream getResourceAsStream(String name) {
	return getClass().getResourceAsStream(name);
    }

    protected void login(final String username, final String password) {
        AuthenticationProvider provider = (AuthenticationProvider) getApplicationContext().getBean("authenticationProvider");
        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
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
//            JcrUtil.dump(node.getNode("workspaces"));
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Node child = itr.nextNode();
                if (!child.getName().equals("jcr:system")) {
                    child.remove();
                }
            }
            session.save();
            session.logout();
        } catch (PathNotFoundException t) {
            // ignore
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected WebApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(context.getServletContext());
    }

    protected Entry assertAndGetEntry(ClientResponse res, int status) {
        assertEquals(status, res.getStatus());
        
        Document<Entry> entryDoc = res.getDocument();
        return entryDoc.getRoot();
    }

    protected ExtensibleElement getVersionedMetadata(Entry entry) {
        return getMetadata(entry, "versioned");
    }
    
    protected ExtensibleElement getGlobalMetadata(Entry entry) {
        return getMetadata(entry, "global");
    }

    private ExtensibleElement getMetadata(Entry entry, String type) {
        QName metadataQ = new QName(AbstractItemCollection.NAMESPACE, "metadata");
        List<ExtensibleElement> extensions = entry.getExtensions(metadataQ);
        ExtensibleElement metadata = null;
        for (ExtensibleElement el : extensions) {
            String id = el.getAttributeValue("id");
            if (type.equals(id))
                metadata = el;
        }
        
        return metadata;
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
        context.setWar(getWebappDirectory().getAbsolutePath());
        server.setHandler(context);
        server.setStopAtShutdown(true);
        
        server.start();
    }

    /**
     * The webapp relative directory varies depending on run from Maven, Eclipse and IDEA
     * this will check each possiblilty to return an existing location
     * @throws IOException 
     */
    protected File getWebappDirectory() throws IOException 
    {
        File f = new File("./target/webapp/");
        File webInf = new File(f, "WEB-INF/");
        // always update web.xml
        File webxml = new File(webInf, "web.xml");
        webxml.mkdirs();
        webxml.delete();
        
        URL url = getClass().getResource("/web.xml");
        assertNotNull("Could not find web.xml on classpath", url);
        
        FileOutputStream out = new FileOutputStream(webxml);
        IOUtils.copy(url.openStream(), out);
        out.close();
        
        return f;
    }
    
}
