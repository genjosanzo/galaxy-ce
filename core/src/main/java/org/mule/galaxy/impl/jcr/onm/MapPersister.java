package org.mule.galaxy.impl.jcr.onm;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.JcrUtil;

public class MapPersister implements FieldPersister {

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        // TODO
        return null;
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
