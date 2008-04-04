package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public abstract class AbstractDao<T extends Identifiable> extends JcrTemplate implements Dao<T> {

    protected String rootNode;
    protected String objectsNodeId;
    protected String idAttributeName;
    protected boolean generateId;
    protected PersisterManager persisterManager;
    protected Class type;
    protected ClassPersister persister;
    
    protected AbstractDao(Class t, String rootNode) throws Exception {
        this(t, rootNode, false);
    }
    
    protected AbstractDao(Class t, String rootNode,  boolean generateId) throws Exception {
        this.rootNode = rootNode;
        this.generateId = generateId;
        this.type = t;
    }

    
    public void setPersisterManager(PersisterManager persisterManager) {
        this.persisterManager = persisterManager;
    }
    
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
                try {
                    doSave(t, session);
                } catch (NotFoundException e) {
                    // TODO Auto-generated catch block
                    throw new RuntimeException(e);
                }
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
    
    @SuppressWarnings("unchecked")
    public List<T> find(final String property, final String value) {
        return (List<T>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doFind(property, value, session);
            }
        });
    }

    protected QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }
    
    public void initialize() throws Exception {
        initalizePersister();
        
        SecurityUtils.doPriveleged(new Runnable() {

            public void run() {
                try {
                    doInitializeInJcrTransaction();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
            
        });
    }

    private void doInitializeInJcrTransaction() throws IOException, RepositoryException {
        JcrUtil.doInTransaction(getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node root = session.getRootNode();
                
                Node objects = getOrCreate(root, rootNode, "galaxy:noSiblings");
                objectsNodeId = objects.getUUID();

                doCreateInitialNodes(session, objects); 
                
                session.save();
                return null;
            }
        });
    }

    protected void initalizePersister() throws Exception {
        persisterManager.getPersisters().put(type.getName(), new DaoPersister(this));
//        this.persister = new ClassPersister(type, rootNode, persisterManager);
//        persisterManager.getClassPersisters().put(type.getName(), persister);
    }

    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
    }
    
    protected abstract void persist(T o, Node node, Session session) throws Exception;
    
    protected T doGet(String id, Session session) throws RepositoryException {
        Node node = findNode(id, session);
        
        if (node == null) {
            return null;
        }
        
        try {
            return build(node, session);
        } catch (Exception e) {
            if (e instanceof RepositoryException) {
                throw ((RepositoryException) e);
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public abstract T build(Node node, Session session) throws Exception;

    @SuppressWarnings("unchecked")
    protected List<T> doListAll(Session session) throws RepositoryException {
        ArrayList<T> objs = new ArrayList<T>();
        for (NodeIterator nodes = findAllNodes(session); nodes.hasNext();) {
            Node node = nodes.nextNode();
            
            try {
                objs.add((T) build(node, session));
            } catch (Exception e) {
                // TODO: not sure what to do here
                if (e instanceof RepositoryException) {
                    throw ((RepositoryException) e);
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }
        return objs;
    }

    protected NodeIterator findAllNodes(Session session) throws RepositoryException {
        return getObjectsNode(session).getNodes();
    }

    protected Node getObjectsNode(Session session) throws RepositoryException {
        return session.getNodeByUUID(objectsNodeId);
    }
    
    protected void doDelete(String id, Session session) throws RepositoryException {
        Node node = findNode(id, session);
        node.remove();
        session.save();
    }
    
    protected void doSave(T t, Session session) 
        throws RepositoryException, NotFoundException {
        String id = t.getId();
        Node node = null;
        
        if (id == null) {
            String genId = generateNodeName(t);
            node = getNodeForObject(getObjectsNode(session), t)
                .addNode(genId, getNodeType());
            node.addMixin("mix:referenceable");
            
            t.setId(ISO9075.decode(getId(t, node, session)));
        } else {
            node = findNode(id, session);
            
            // the user supplied a new ID
            if (node == null && !generateId) {
                node = getNodeForObject(getObjectsNode(session), t).addNode(ISO9075.encode(getObjectNodeName(t)), getNodeType());
                node.addMixin("mix:referenceable");
            }
        }
        
        if (node == null) throw new NotFoundException(t.getId());
        
        try {
            persist(t, node, session);
        } catch (Exception e) {
            if (e instanceof RepositoryException) {
                throw ((RepositoryException) e);
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    protected String generateNodeName(T t) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    protected String getNodeType() {
        return "nt:unstructured";
    }

    /**
     * Allow implementations to segment nodes so that performance doesn't suffer.
     * @param node 
     * @param t
     * @return
     * @throws RepositoryException 
     */
    protected Node getNodeForObject(Node node, T t) throws RepositoryException {
        return node;
    }

    protected String getId(T t, Node node, Session session) throws RepositoryException {
        return node.getName();
    }

    protected String getObjectNodeName(T t) {
        throw new UnsupportedOperationException();
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        if (isIdNodeName()) {
            try {
                return getObjectsNode(session).getNode(id);
            } catch (PathNotFoundException e) {
                return null;
            }
        } else {
            QueryManager qm = getQueryManager(session);
            Query q = qm.createQuery("/jcr:root/" + rootNode + "/*[@" + idAttributeName + "='" + id + "']", Query.XPATH);

            QueryResult qr = q.execute();
            
            NodeIterator nodes = qr.getNodes();
            if (!nodes.hasNext()) {
                return null;
            }
            
            return nodes.nextNode();
        }
    }

    protected boolean isIdNodeName() {
        return true;
    }

    protected List<T> doFind(String property, String value, Session session) throws RepositoryException {
        String stmt = "/*/" + rootNode + "/*[@" + property + "='" + value + "']";
        return query(stmt, session);
    }
    
    protected List<T> query(String stmt, Session session) throws RepositoryException, InvalidQueryException {
        return query(stmt, session, -1);
    }
    
    protected List<T> query(String stmt, Session session, int maxResults) throws RepositoryException, InvalidQueryException {
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt, Query.XPATH);
        
        QueryResult qr = q.execute();
        
        List<T> values = new ArrayList<T>();
        
        int i = 0;
        for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
            try {
                values.add(build(nodes.nextNode(), session));
            } catch (Exception e) {
                if (e instanceof RepositoryException) {
                    throw ((RepositoryException) e);
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
            if (maxResults >= 0 && values.size() == i) {
                break;
            }
            i++;
        }

        return values;
    }

}
