package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;

import org.mule.galaxy.impl.jcr.JcrUtil;

public class DefaultPersister implements FieldPersister {

    public Object build(Node n, String property) throws Exception {
        return JcrUtil.getProperty(property, n);
    }

    public void persist(Object o, Node n, String property) throws Exception {
        JcrUtil.setProperty(property, o, n);
    }

    
}
