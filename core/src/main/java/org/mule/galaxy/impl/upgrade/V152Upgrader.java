package org.mule.galaxy.impl.upgrade;

import java.io.IOException;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.JcrItem;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.security.AccessException;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.SessionFactory;

public class V152Upgrader extends Upgrader {

    private final Log log = LogFactory.getLog(getClass());
    
    private Registry registry;

    private SessionFactory sessionFactory;
    
    @Override
    public void doUpgrade(final int version, final Session session, final Node root) throws Exception {
        if (version >= 5) return;
        
        log.info("Upgrading to version 1.5.2....");
        
        final QueryManager qm = session.getWorkspace().getQueryManager();
        
        JcrUtil.doInTransaction(sessionFactory, new JcrCallback() {

            public Object doInJcr(Session arg0) throws IOException, RepositoryException {
                upgradeLinks(root, qm, session);
                return null;
            }
            
        });

        log.info("Upgrade to version 1.5.2 complete!");
    }

    /**
     * Stores a cache value indicating whether an item has links or not
     * @param session 
     * @throws RepositoryException 
     */
    private void upgradeLinks(Node root, QueryManager qm, Session session) throws RepositoryException {
        Query q = qm.createQuery("/jcr:root/links/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node l = itr.nextNode();

            String itemId = (String) JcrUtil.getProperty("item", l);
            String property = (String) JcrUtil.getProperty("property", l);
            
            updateItem(itemId, property, session);
            
            String linkedTo = (String) JcrUtil.getProperty("linkedTo", l);
            if (linkedTo != null) {
                updateItem(linkedTo, property, session);
            } else {
                try {
                    Item item = registry.getItemById(itemId);
                    Item resolve = registry.resolve(item, (String) JcrUtil.getProperty("linkedToPath", l));
                    if (resolve != null) {
                        l.setProperty("linkedTo", resolve.getId());
                    }
                } catch (NotFoundException e) {
                    l.remove();
                } catch (RegistryException e) {
                    throw new RuntimeException(e);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateItem(String itemId, String property, Session session) throws RepositoryException {
        itemId = itemId.substring(itemId.indexOf(Registry.WORKSPACE_MANAGER_SEPARATOR)+1);
            
        try {
            Node node = session.getNodeByUUID(itemId);
            JcrUtil.setProperty(property, true, node);
            JcrItem.ensureProperty(node, property);
        } catch (ItemNotFoundException e) {
        }
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }         

}
