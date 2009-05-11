package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.onm.ClassPersister;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.mule.galaxy.impl.jcr.onm.PersisterManager;
import org.mule.galaxy.mapping.OneToMany;

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
        if (!parentField.equals("")) {
            String parentId = n.getUUID();
            String rootNode = fd.getClassPersister().getPath();
            
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery("/jcr:root/" + rootNode 
                                     + "/*[@" + parentField + "='" + parentId + "']", Query.XPATH);
            
            QueryResult result = q.execute();
            
            ClassPersister cp = persisterManager.getClassPersister(fd.getClassPersister().getType().getName());
            for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                Object obj = cp.build(nodes.nextNode(), session);
                collection.add(obj);
            }
        } else {
           try {
               FieldPersister fp = persisterManager.getPersister(fd.getOneToMany().componentType());
               Property property = n.getProperty(fd.getName() +"");
               
               for (Value v : property.getValues()) {
                   collection.add(fp.build(v.getString(), fd, session));
               }
           } catch (PathNotFoundException e) {
           }
        }
        
        return collection;
    }

    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        OneToMany otm = fd.getOneToMany();
        if (otm == null) {
            throw new UnsupportedOperationException("You must supply a @OneToMany annotation for " 
                                                    + fd.getName() + " on " + fd.getClassPersister().getType());
        }
        
        if (otm.treatAsField()) {
            JcrUtil.setProperty(fd.getName(), o, n);
        } else if (fd.getOneToMany().mappedBy().equals("")) {
            List<String> values = new ArrayList<String>();
            
            Collection c = (Collection) o;
            
            if (c != null) {
                for (Object cObj : c) {
                    values.add(((Identifiable) cObj).getId());
                }
            }
            
            n.setProperty(fd.getName(), (String[]) values.toArray(new String[values.size()]));
        }
    }

}
