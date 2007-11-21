package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.springmodules.jcr.JcrCallback;

public abstract class AbstractDao<T extends Identifiable> implements Dao<T> {
    protected JcrRegistryImpl registry;
    
    public void setRegistry(JcrRegistryImpl registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public T get(final String id) {
        return (T) registry.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doGet(id, session);
            }
        });
    }

    public void save(final T t) {
        registry.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                doSave(t, session);
                return null;
            }
        });
    }
    
    public void delete(final String id) {
        registry.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                doDelete(id, session);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<T> listAll() {
        return (List<T>) registry.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doListAll(session);
            }
        });
    }

    protected abstract void doSave(T t, Session session) throws RepositoryException;

    protected abstract T doGet(String id, Session session) throws RepositoryException;

    protected abstract List<T> doListAll(Session session) throws RepositoryException;
    
    protected void doDelete(String id, Session session) throws RepositoryException {
        Node node = registry.getNodeByUUID(id);
        node.remove();
        session.save();
    }
}
