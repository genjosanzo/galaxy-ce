package org.mule.galaxy.impl.jcr;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.persistence.PersistenceManager;
import org.mule.galaxy.impl.jcr.onm.ClassPersister;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.mule.galaxy.impl.jcr.onm.OneToMany;
import org.mule.galaxy.impl.jcr.onm.PersisterManager;

public class CollectionPersister implements FieldPersister {

    private Class implementation;
    private PersisterManager persisterManager;
    
    public CollectionPersister(Class implementation, PersisterManager persisterManager) {
        super();
        this.implementation = implementation;
        this.persisterManager = persisterManager;
    }

    @SuppressWarnings("unchecked")
    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        Collection collection = (Collection) implementation.newInstance();
        
        OneToMany otm = fd.getOneToMany();
        if (otm == null) {
            throw new UnsupportedOperationException("You must supply a @OneToMany annotation for " 
                                                    + fd.getName() + " on " + fd.getClassPersister().getType());
        }
        
        if (otm.treatAsField()) {
            return JcrUtil.getProperty(fd.getName(), n);
        }
        
        String parentField = otm.mappedBy();
        String parentId = n.getUUID();
        String rootNode = fd.getClassPersister().getPath();
        
        QueryManager qm = session.getWorkspace().getQueryManager();
        Query q = qm.createQuery("/*/" + rootNode 
                                 + "/*[@" + parentField + "='" + parentId + "']", Query.XPATH);
        
        QueryResult result = q.execute();
        ClassPersister cp = persisterManager.getClassPersisters().get(fd.getClassPersister().getType().getName());
        
        for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
            Object obj = cp.build(nodes.nextNode(), session);
            collection.add(obj);
        }
        
        return collection;
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        OneToMany otm = fd.getOneToMany();
        if (otm == null) {
            throw new UnsupportedOperationException("You must supply a @OneToMany annotation for " 
                                                    + fd.getName() + " on " + fd.getClassPersister().getType());
        }
        
        if (otm.treatAsField()) {
            JcrUtil.setProperty(fd.getName(), o, n);
        }
    }

}
