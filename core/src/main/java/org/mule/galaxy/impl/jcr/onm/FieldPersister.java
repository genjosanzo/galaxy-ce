package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;

/**
 * Provides a way to map class fields to Nodes.
 */
public interface FieldPersister {

    public abstract void persist(Object o, Node n, String property) throws Exception;

    public abstract Object build(Node n, String property) throws Exception;

}
