package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.JcrUtil;

public class DaoPersister implements FieldPersister {
    private Dao<? extends Identifiable> dao;

    public DaoPersister(Dao<? extends Identifiable> dao) {
        super();
        this.dao = dao;
    }

    public Object build(Node n, String property) throws Exception {
        String id = JcrUtil.getStringOrNull(n, property);
        if (id == null) {
            return null;
        }
        
        return dao.get(id);
    }

    public void persist(Object o, Node n, String property) throws Exception {
        if (o == null) {
            n.setProperty(property, (String) null);
        } else {
            n.setProperty(property, ((Identifiable) o).getId());
        }
    }
}
