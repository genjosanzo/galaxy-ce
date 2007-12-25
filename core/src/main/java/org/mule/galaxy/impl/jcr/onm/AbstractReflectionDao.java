package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.NotFoundException;

public abstract class AbstractReflectionDao<T extends Identifiable> extends AbstractDao<T> {

    private ClassPersister persister;
    private String rootNode;
    private String objectsNodeId;
    private String idNode;
    private boolean useNodeId;
    private String objectNodeName;
    private PersisterManager persisterManager;
    private Class type;
    
    protected AbstractReflectionDao(Class t, String rootNode, boolean useNodeId) throws Exception {
        this(t, rootNode, useNodeId, "id");
    }
    
    protected AbstractReflectionDao(Class t, String rootNode, String idNode) throws Exception {
        this(t, rootNode, false, idNode);
    }
    
    private AbstractReflectionDao(Class t, String rootNode, boolean useNodeId, String idNode) throws Exception {
        this.rootNode = rootNode;
        this.idNode = idNode;
        this.useNodeId = useNodeId;
        this.type = t;
        
        objectNodeName = t.getSimpleName();
        objectNodeName = objectNodeName.substring(0, 1).toLowerCase() + objectNodeName.substring(1);
    }
    
    public void initialize() throws Exception {
        persisterManager.getPersisters().put(type.getName(), new DaoPersister(this));
        this.persister = new ClassPersister(type, rootNode, persisterManager);
        persisterManager.getClassPersisters().put(type.getName(), persister);
        Session session = getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        Node objects = getOrCreate(root, rootNode);
        objectsNodeId = objects.getUUID();

        doCreateInitialNodes(session, objects); 
        
        session.save();
        session.logout(); 
    }

    public void setPersisterManager(PersisterManager persisterManager) {
        this.persisterManager = persisterManager;
    }

    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
    }
    
    protected void persist(T o, Node node, Session session) throws Exception {
        persister.persist(o, node, session);
    }
    
    @Override
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
    public T build(Node node, Session session) throws Exception {
        T t = (T) persister.build(node, session);
        if (useNodeId) {
            t.setId(node.getUUID());
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> doListAll(Session session) throws RepositoryException {
        ArrayList<T> objs = new ArrayList<T>();
        for (NodeIterator nodes = getObjectsNode().getNodes(); nodes.hasNext();) {
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

    protected Node getObjectsNode() {
        return getNodeByUUID(objectsNodeId);
    }
    
    protected void doDelete(String id, Session session) throws RepositoryException {
        Node node = findNode(id, session);
        node.remove();
        session.save();
    }
    
    @Override
    protected void doSave(T t, Session session) 
        throws RepositoryException, NotFoundException {
        String id = t.getId();
        Node node = null;
        
        if (id == null) {
            node = getObjectsNode().addNode(getObjectNodeName(t));
            node.addMixin("mix:referenceable");
            id = getId(t, node, session);
            t.setId(id);
        } else {
            node = findNode(id, session);
            
            // the user supplied a new ID
            if (node == null && !useNodeId) {
                node = getObjectsNode().addNode(getObjectNodeName(t));
                node.addMixin("mix:referenceable");
            }
        }
        
        if (node == null) throw new NotFoundException(t.getId());
        
        try {
            persister.persist(t, node, session);
        } catch (Exception e) {
            if (e instanceof RepositoryException) {
                throw ((RepositoryException) e);
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    protected String getId(T t, Node node, Session session) throws RepositoryException {
        if (useNodeId) {
            return node.getUUID();
        }
        
        return null;
    }

    protected String getObjectNodeName(T t) {
        return objectNodeName;
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        if (useNodeId) {
            return getNodeByUUID(id);
        } 
        
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery("/*/" + rootNode + "/*[@" + idNode + "='" + id + "']", Query.XPATH);
        
        QueryResult qr = q.execute();
        
        NodeIterator nodes = qr.getNodes();
        if (!nodes.hasNext()) {
            return null;
        }
        
        return nodes.nextNode();
    }

    @Override
    protected List<T> doFind(String property, String value, Session session) throws RepositoryException {
        String stmt = "/*/" + rootNode + "/*[@" + property + "='" + value + "']";
        return query(stmt, session);
    }

    protected List<T> query(String stmt, Session session) throws RepositoryException, InvalidQueryException {
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt, Query.XPATH);
        
        QueryResult qr = q.execute();
        
        List<T> values = new ArrayList<T>();
        
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
        }

        return values;
    }


    
}
