package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public abstract class AbstractDao<T extends Identifiable> extends JcrTemplate implements Dao<T> {

    
    @SuppressWarnings("unchecked")
    public T get(final String id) {
        return (T) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doGet(id, session);
            }
        });
    }

    public void save(final T t) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                doSave(t, session);
                session.save();
                return null;
            }
        });
    }
    
    public void delete(final String id) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                doDelete(id, session);
                session.save();
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<T> listAll() {
        return (List<T>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doListAll(session);
            }
        });
    }
    
    protected QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }

    protected abstract void doSave(T t, Session session) throws RepositoryException;

    protected abstract T doGet(String id, Session session) throws RepositoryException;

    protected abstract List<T> doListAll(Session session) throws RepositoryException;
    
    protected abstract void doDelete(String id, Session session) throws RepositoryException;
}
