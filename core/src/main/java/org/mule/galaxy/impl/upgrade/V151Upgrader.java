package org.mule.galaxy.impl.upgrade;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class V151Upgrader extends Upgrader {

    private final Log log = LogFactory.getLog(getClass());
    
    private TypeManager typeManager;
    
    @Override
    public void doUpgrade(int version, Session session, Node root) throws Exception {
        if (version >= 4) return;
        
        log.info("Upgrading to version 1.5.1....");
        
        QueryManager qm = session.getWorkspace().getQueryManager();

        upgradeType(root, qm);

        log.info("Upgrade to version 1.5.1 complete!");
    }

    /**
     * Changes the lifecycle fields to a metadata property on the artifact
     */
    private void upgradeType(Node root, QueryManager qm) throws Exception {
        Query q = qm.createQuery("//element(*, galaxy:artifact)", Query.XPATH);
        Type defaultType = typeManager.getDefaultType();
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node a = itr.nextNode();
            
            Object type = JcrUtil.getProperty("type", a);
            if (type == null) {
                JcrUtil.setProperty("type", defaultType.getId(), a);
            }
        }

    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

}
