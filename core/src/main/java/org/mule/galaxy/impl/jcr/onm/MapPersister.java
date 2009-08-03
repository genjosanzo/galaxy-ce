package org.mule.galaxy.impl.jcr.onm;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.JcrUtil;

public class MapPersister implements FieldPersister {

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        try {
            n = n.getNode(fd.getName());
        } catch (PathNotFoundException e) {
            return null;
        }
        
        // technically could be the wrong parameterization, but java loses all the info so who cares
        Map<String,Object> map = new HashMap<String, Object>();
        
        // TODO: This is UGLY, but it'll do for now.
        for (PropertyIterator props = n.getProperties(); props.hasNext();) {
            Property child = props.nextProperty();
            
            if (!child.getName().startsWith("jcr:")) {
                map.put(child.getName(), JcrUtil.getProperty(child.getName(), n));
            }
        }
        return map;
    }

    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        Node mapNode = JcrUtil.getOrCreate(n, fd.getName());

        if (o == null){
            mapNode.remove();
            return;
        }
        
        Map<?,?> map = (Map<?,?>) o;
        
        // TODO: make this lazy and write a LazyNodeMap
        for (Map.Entry<?,?> e : map.entrySet()) {
            // TODO: handle more complex maps
            JcrUtil.setProperty((String) e.getKey(), e.getValue(), mapNode);
        }

    }

    
}
