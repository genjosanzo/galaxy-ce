package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.Identifiable;

public class AbstractReflectionDao<T extends Identifiable> extends AbstractDao<T> {

    protected AbstractReflectionDao(Class<T> t, String rootNode) throws Exception {
        this(t, rootNode, false);
    }
    
    protected AbstractReflectionDao(Class<T> t, String rootNode,  boolean generateId) throws Exception {
        super(t, rootNode, generateId);
    }

    protected void initalizePersister() throws Exception {
        persisterManager.getPersisters().put(type.getName(), new DaoPersister(this));
        this.persister = new ClassPersister(type, rootNode, persisterManager);
        persisterManager.getClassPersisters().put(type.getName(), persister);
    }
    
    protected void persist(T o, Node node, Session session) throws Exception {
        persister.persist(o, node, session);
    }

    @SuppressWarnings("unchecked")
    public T build(Node node, Session session) throws Exception {
        T t = (T) persister.build(node, session);
        if (generateId) {
            t.setId(getId(t, node, session));
        }
        return t;
    }

}
