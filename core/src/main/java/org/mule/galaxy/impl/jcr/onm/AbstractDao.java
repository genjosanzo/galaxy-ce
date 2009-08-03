package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
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
    protected Class<T> type;
    
    protected AbstractDao(Class<T> t, String rootNode) throws Exception {
        this(t, rootNode, false);
    }
    
    protected AbstractDao(Class<T> t, String rootNode,  boolean generateId) throws Exception {
        this.rootNode = rootNode;
        this.generateId = generateId;
        this.type = t;
    }
    
    public Class<T> getTypeClass() {
    return type;
    }

    @SuppressWarnings("unchecked")
    public T get(final String id) throws NotFoundException {
        T t =  (T) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return doGet(id, session);
            }
        });
        
        if (t == null) {
            throw new NotFoundException(id);
        }
        
        return t;
    }

    public void save(final T t) throws DuplicateItemException, NotFoundException {
        executeAndDewrap(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    doSave(t, session);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                } catch (ItemExistsException e) {
                    throw new RuntimeException(new DuplicateItemException(e));
                }
                
                session.save();
                
                return null;
            }
        });
    }
    
    private void executeAndDewrap(JcrCallback jcrCallback) throws DuplicateItemException, NotFoundException {
        try {
            execute(jcrCallback);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            
            if (cause instanceof DuplicateItemException) {
                throw (DuplicateItemException) cause;
            } else if (cause instanceof NotFoundException) {
                throw (NotFoundException) cause;
            } else {
                throw e;
            }
        }
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
    
    public List<T> find(final String property, final String value) {
        String stmt = "/*/" + rootNode + "/*[";
        
        stmt = buildFindPredicate(stmt, property, value);
       
        stmt += "]";
        
        return doQuery(stmt);
    }
    

    
    public List<T> find(final Map<String, Object> criteria) {
        String stmt = "/*/" + rootNode + "/*[";
        String join = "";
        for (Map.Entry<String, Object> e : criteria.entrySet()) {
            stmt += join;
            join = " and ";
            
            stmt = buildFindPredicate(stmt, e.getKey(), e.getValue());
        }
        stmt += "]";
        return doQuery(stmt);
    }

    private String buildFindPredicate(String stmt, String key, Object value) {
        if (value == null) {
            stmt += "not(@" + key + ")";
        } else if (value.toString().startsWith("%") || value.toString().endsWith("%")) {
            stmt += "jcr:like(@" + key + ", '" + value + "')";
        } else {
            stmt += "@" + key + "='" + value + "'";
        }
        return stmt;
    }
    
    protected List<T> doQuery(final String stmt) {
        return doQuery(stmt, 0, -1);
    }

    @SuppressWarnings("unchecked")
    protected List<T> doQuery(final String stmt, final int start, final int max) {
        return (List<T>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return query(stmt, session, start, max);
            }
        });
    }
    
    protected QueryManager getQueryManager(Session session) throws RepositoryException {
        return session.getWorkspace().getQueryManager();
    }
    
    public void initialize() throws Exception {
        SecurityUtils.doPriveleged(new Runnable() {

            public void run() {
                try {
                    doInitializePriveleged();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
            
        });
    }

    protected void doInitializePriveleged() throws IOException, RepositoryException {
        JcrUtil.doInTransaction(getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                doInitializeInJcrTransaction(session); 
                
                session.save();
                return null;
            }
        });
    }

    protected void doInitializeInJcrTransaction(Session session) throws RepositoryException,
        UnsupportedRepositoryOperationException {
        Node root = session.getRootNode();
        
        Node objects = getOrCreate(root, rootNode, "galaxy:noSiblings");
        objectsNodeId = objects.getUUID();

        doCreateInitialNodes(session, objects);
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

    public abstract T build(Node node, Session session) throws Exception;

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
        doDeleteNode(session, node);
    }

    protected void doDeleteNode(Session session, Node node) throws RepositoryException {
        node.remove();
        session.save();
    }
    
    protected void doSave(T t, Session session) 
        throws RepositoryException, NotFoundException {
        String id = t.getId();
        Node node = null;
        boolean isNew = true;
        boolean isMoved = false;
        
        if (id == null) {
            String genId = generateNodeName(t);
            node = getNodeForObject(getObjectsNode(session), t).addNode(genId, getNodeType());
            node.addMixin("mix:referenceable");
            
            t.setId(ISO9075.decode(getId(t, node, session)));
        } else {
            node = findNode(id, session);
            
            // the user supplied a new ID, use it to create a node
            if (node == null && !generateId) {
                node = getNodeForObject(getObjectsNode(session), t)
                          .addNode(ISO9075.encode(getObjectNodeName(t)), getNodeType());
                node.addMixin("mix:referenceable");
            }  else {
                isNew = false;

                String newName = generateNodeName(t); 
                if (!newName.equals(node.getName())) {
                    move(session, node, newName);
                    node = session.getNodeByUUID(node.getUUID());
                    isMoved = true;
                }
            }
            
        }
        
        if (node == null) throw new NotFoundException(t.getId());
        
        doSave(t, node, isNew, isMoved, session);
    }

    protected void move(Session session, Node node, String newName) throws RepositoryException {
        session.move(node.getPath(), node.getParent().getPath() + "/" + newName);
    }

    protected void doSave(T t, Node node, boolean isNew, boolean isMoved, Session session) throws RepositoryException {
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
        if (t.getId() != null) {
            return t.getId();
        }
        
        return UUID.randomUUID().toString();
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
        return node.getUUID();
    }

    protected String getObjectNodeName(T t) {
        throw new UnsupportedOperationException();
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        return session.getNodeByUUID(id);
    }

    public String getRootNodeName() {
        return rootNode;
    }

    protected boolean isIdNodeName() {
        return true;
    }
    
    protected List<T> query(String stmt, Session session) throws RepositoryException, InvalidQueryException {
        return query(stmt, session, 0, -1);
    }
    
    protected List<T> query(String stmt, Session session, int start, int maxResults) throws RepositoryException, InvalidQueryException {
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt, Query.XPATH);
        
        QueryResult qr = q.execute();
        
        List<T> values = new ArrayList<T>();
        
        NodeIterator iterator = qr.getNodes();
        iterator.skip(start);
        
        int i = 0;
        for (NodeIterator nodes = iterator; nodes.hasNext();) {
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
