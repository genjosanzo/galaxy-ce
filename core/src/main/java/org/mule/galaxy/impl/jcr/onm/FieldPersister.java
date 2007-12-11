package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * Provides a way to map class fields to Nodes.
 */
public interface FieldPersister {

    void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception;
    
    Object build(Node node, FieldDescriptor fd, Session session) throws Exception;

}
