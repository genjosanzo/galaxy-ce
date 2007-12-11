package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.JcrUtil;

public class DaoPersister implements FieldPersister {
    private Dao<? extends Identifiable> dao;

    public DaoPersister(Dao<? extends Identifiable> dao) {
        super();
        this.dao = dao;
    }

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        String id = JcrUtil.getStringOrNull(n, fd.getName());
        if (id == null) {
            return null;
        }
        
        return dao.get(id);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        String name = fd.getName();
        if (o == null) {
            n.setProperty(name, (String) null);
        } else {
            n.setProperty(name, ((Identifiable) o).getId());
        }
    }
}
