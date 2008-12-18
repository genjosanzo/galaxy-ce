package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.impl.jcr.AbstractJcrItem;
import org.mule.galaxy.impl.jcr.JcrRegistry;
import org.mule.galaxy.impl.jcr.JcrUtil;

public class V152Upgrader extends Upgrader {

    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public void doUpgrade(int version, Session session, Node root) throws Exception {
        if (version >= 5) return;
        
        log.info("Upgrading to version 1.5.2....");
        
        QueryManager qm = session.getWorkspace().getQueryManager();

        upgradeLinks(root, qm, session);

        log.info("Upgrade to version 1.5.2 complete!");
    }

    /**
     * Stores a cache value indicating whether an item has links or not
     * @param session 
     */
    private void upgradeLinks(Node root, QueryManager qm, Session session) throws Exception {
        Query q = qm.createQuery("/jcr:root/links/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node l = itr.nextNode();

            String itemId = (String) JcrUtil.getProperty("item", l);
            String property = (String) JcrUtil.getProperty("property", l);
            
            updateItem(itemId, property, session);
            
            String linkedTo = (String) JcrUtil.getProperty("linkedTo", l);
            if (linkedTo != null) {
                updateItem(linkedTo, property, session);
            }
        }
    }

    private void updateItem(String itemId, String property, Session session) throws Exception {
        itemId = itemId.substring(itemId.indexOf(JcrRegistry.WORKSPACE_MANAGER_SEPARATOR)+1);
            
        Node node = session.getNodeByUUID(itemId);
        JcrUtil.setProperty(property, true, node);
        AbstractJcrItem.ensureProperty(node, property);
    }         

}
