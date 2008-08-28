package org.mule.galaxy.impl.upgrade.v11;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.upgrade.Upgrader;

public class IndexUpgrader extends Upgrader {

    /**
     * Sets index field to true on property descriptors if they refer to an Index.
     */
    @Override
    public void doUpgrade(int version, Session session, Node root) throws RepositoryException {
        if (version >= 2) return;
        
        Node pds = root.getNode("propertyDescriptors");
        QueryManager qm = session.getWorkspace().getQueryManager();
        
        for (NodeIterator itr = pds.getNodes(); itr.hasNext();) {
            Node pd = itr.nextNode();
            String name = JcrUtil.getStringOrNull(pd, "name");
            
            Query q = qm.createQuery("//*[@jcr:uuid=" + root.getUUID() +"]/indexes//[@property='" + name + "']", Query.XPATH);
            
            if (q.execute().getNodes().getSize() > 0) {
                pd.setProperty("index", true);
            }
        }
    }

}
