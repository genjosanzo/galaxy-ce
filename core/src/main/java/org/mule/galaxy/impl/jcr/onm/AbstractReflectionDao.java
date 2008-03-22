package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.springmodules.jcr.JcrCallback;

public abstract class AbstractReflectionDao<T extends Identifiable> extends AbstractDao<T> {

    private String objectNodeName;
    
    protected AbstractReflectionDao(Class t, String rootNode) throws Exception {
        this(t, rootNode, false);
    }
    
    protected AbstractReflectionDao(Class t, String rootNode,  boolean generateId) throws Exception {
        super(t, rootNode, generateId);
        
        objectNodeName = t.getSimpleName();
        objectNodeName = objectNodeName.substring(0, 1).toLowerCase() + objectNodeName.substring(1);
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

    protected String getObjectNodeName(T t) {
        return objectNodeName;
    }    
}
