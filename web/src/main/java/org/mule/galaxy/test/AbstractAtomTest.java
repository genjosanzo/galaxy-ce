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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.atom.ItemCollection;
import org.mule.galaxy.impl.index.IndexManagerImpl;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.TypeManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springmodules.jcr.SessionFactory;

public abstract class AbstractAtomTest extends TestCase {
    
    protected Registry registry;
    protected TypeManager typeManager;
    protected Provider provider;
    protected Abdera abdera = new Abdera();
    protected Factory factory = abdera.getFactory();
    private Server server;
    private WebAppContext context;
    protected SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        System.setProperty("galaxy.data", "./target/galaxy-data");
        deleteIfExists(new File("target/galaxy-data/repository"));
        deleteIfExists(new File("target/galaxy-data/version"));
        deleteIfExists(new File("target/galaxy-data/workspaces"));
        
        super.setUp();
        initializeJetty();
        
        registry = (Registry) getApplicationContext().getBean("registry");
        typeManager = (TypeManager) getApplicationContext().getBean("typeManager");
        sessionFactory = (SessionFactory) getApplicationContext().getBean("sessionFactory");
    }

    private void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            FileUtil.delete(file);
        }
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

    protected Item getTestWorkspace() throws RegistryException, AccessException {
        Collection<Item> workspaces = registry.getItems();
        return workspaces.iterator().next();
    }
    
    protected Item importFile(InputStream stream, String name, String version, String contentType)
        throws Exception {
        
        Item workspace = getTestWorkspace();
        
        return importFile(workspace, stream, name, version, contentType);
    }

    protected Item importFile(Item workspace, InputStream stream, String name, String version,
                            String contentType) throws DuplicateItemException, RegistryException,
            PolicyException, PropertyException, AccessException, NotFoundException {
        NewItemResult result = workspace.newItem(name, typeManager.getType(TypeManager.ARTIFACT));
        Item artifact = (Item) result.getItem();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("artifact", new Object[] { stream, contentType });
        NewItemResult ar = artifact.newItem(version, typeManager.getType(TypeManager.ARTIFACT_VERSION), props);

        return (Item) ar.getItem();
    }

    protected WebApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(context.getServletContext());
    }

    protected Entry assertAndGetEntry(ClientResponse res, int status) {
        assertEquals(status, res.getStatus());
        
        Document<Entry> entryDoc = res.getDocument();
        return entryDoc.getRoot();
    }

    protected Entry createEntry(String name, String type) {
        // Create an Entry to represent a workspace
        Entry entry = factory.newEntry();
        entry.setTitle(name);
        entry.setUpdated(new Date());
        entry.addAuthor("Ignored");
        entry.setId(factory.newUuidUri());
        entry.setContent("");
        
        Element wInfo = factory.newElement(new QName(ItemCollection.NAMESPACE, "item-info"));
        wInfo.setAttributeValue("name", name);
        wInfo.setAttributeValue("type", type);
        entry.addExtension(wInfo);
        return entry;
    }    

    protected ExtensibleElement getMetadata(Entry entry) {
        QName metadataQ = new QName(ItemCollection.NAMESPACE, "metadata");
        List<ExtensibleElement> extensions = entry.getExtensions(metadataQ);
        for (ExtensibleElement el : extensions) {
            return el;
        }
        return null;
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
