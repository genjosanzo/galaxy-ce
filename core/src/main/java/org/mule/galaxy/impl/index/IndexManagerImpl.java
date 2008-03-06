package org.mule.galaxy.impl.index;

import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.ActivityManager.EventType;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.index.Indexer;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.util.LogUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.SessionFactory;
import org.springmodules.jcr.SessionFactoryUtils;

public class IndexManagerImpl extends AbstractReflectionDao<Index> 
    implements IndexManager, ApplicationContextAware {
    private Logger LOGGER = LogUtils.getL7dLogger(IndexManagerImpl.class);

    private ContentService contentService;

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    
    private ThreadPoolExecutor executor;

    private Registry registry;

    private ApplicationContext context;
    
    private ActivityManager activityManager;
    
    private boolean destroyed = false;
    
    public IndexManagerImpl() throws Exception {
        super(Index.class, "indexes", false);
    }

    @Override
    public void save(Index t) {
        save(t, false);
    }

    public void save(Index t, boolean block) {
        super.save(t);
        
        if (block) {
            getIndexer(t).run();
        } else {
            reindex(t);
        }
    }
    @SuppressWarnings("unchecked")
    public Collection<Index> getIndexes() {
        return listAll();
    }

    @Override
    protected String getObjectNodeName(Index t) {
        return t.getId();
    }

    @Override
    public Index build(Node node, Session session) throws Exception {
        Index i = super.build(node, session);
        i.setId(node.getName());
        return i;
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        try {
            return getObjectsNode(session).getNode(id);
        } catch (PathNotFoundException e) {
            return null;
        }
    }
    
    @Override
    protected String getNodeType() {
        return "galaxy:index";
    }

    @SuppressWarnings("unchecked")
    public Set<Index> getIndexes(final ArtifactVersion av) {
        return (Set<Index>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                StringBuilder qstr = new StringBuilder("//element(*, galaxy:index)");
                
                QName dt = av.getParent().getDocumentType();
                if (dt == null) {
                    qstr.append("[@mediaType=")
                       .append(JcrUtil.stringToXPathLiteral(av.getParent().getContentType().toString()))
                       .append("]");
                } else {
                    qstr.append("[@documentTypes=")
                        .append(JcrUtil.stringToXPathLiteral(dt.toString()))
                        .append("]");
                }
                
                Query query = qm.createQuery(qstr.toString(), Query.XPATH);
                
                QueryResult result = query.execute();
                
                Set<Index> indices = new HashSet<Index>();
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    Node node = nodes.nextNode();
//                    JcrUtil.dump(node);
                    try {
                        indices.add(build(node, session));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return indices;
            }
        });
    }

    public Index getIndex(final String id) throws NotFoundException {
        Index i = get(id);
        if (i == null) {
            throw new NotFoundException(id);
        }
        return i;
    }

    
    @Override
    public void initialize() throws Exception {
        super.initialize();
        
        executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, queue);
        executor.prestartAllCoreThreads();
    }

    public void destroy() throws Exception {
        LOGGER.log(Level.FINE, "Starting IndexManager.destroy() with " + executor.getQueue().size() + " indexing jobs left");
        if (destroyed) return;
        
        executor.shutdown();
        destroyed = true;

        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // TODO finish reindexing on startup?
        List<Runnable> tasks = executor.shutdownNow();
        
        if (tasks.size() > 0) {
            LOGGER.warning("Could not shut down indexer! Indexing was still going.");
        }
    }

    private void reindex(final Index idx) {
        Runnable runnable = getIndexer(idx);
        
        if (!queue.add(runnable)) handleIndexingException(new Exception("Could not add indexer to queue."));
    }

    private Runnable getIndexer(final Index idx) {
        Runnable runnable = new Runnable() {

            public void run() {
                Session session = null;
                boolean participate = false;
                SessionFactory sf = getSessionFactory();
                if (TransactionSynchronizationManager.hasResource(sf)) {
                    // Do not modify the Session: just set the participate
                    // flag.
                    participate = true;
                } else {
                    logger.debug("Opening reeindexing session");
                    session = SessionFactoryUtils.getSession(sf, true);
                    TransactionSynchronizationManager.bindResource(sf, sf.getSessionHolder(session));
                }

                try {
                    
                    findAndReindex(session, idx);
                } catch (RepositoryException e) {
                    handleIndexingException(e);
                } finally {
                    if (!participate) {
                        TransactionSynchronizationManager.unbindResource(sf);
                        logger.debug("Closing reindexing session");
                        SessionFactoryUtils.releaseSession(session, sf);
                    }
                }
            }
        };
        return runnable;
    }

    protected void findAndReindex(Session session, Index idx) throws RepositoryException
    {
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query(Artifact.class)
                // TODO Restriction.in causes an NPE on reindexing?!
                .add(Restriction.eq("contentType", Arrays.asList(idx.getMediaType())));
                // TODO documentType lookup won't work for non-xml artifacts
                //.add(Restriction.in("documentType", idx.getDocumentTypes()));

        try
        {
            Set results = getRegistry().search(q).getResults();

            logActivity("Reindexing " + idx.getId() + " for " + results.size() + " artifacts.");

            for (Object o : results) {
                Artifact a = (Artifact) o;

                for (ArtifactVersion v : a.getVersions()) {
                    ContentHandler ch = contentService.getContentHandler(v.getParent().getContentType());

                    try {
                        getIndexer(idx.getIndexer()).index(v, ch, idx);
                    } catch (IndexException e) {
                        handleIndexingException(idx, e);
                    } catch (IOException e) {
                        handleIndexingException(idx, e);
                    }
                }

                session.save();
            }
        } catch (QueryException e) {
            logActivity("Could not reindex documents for index " + idx.getId(), e);
        } catch (RegistryException e) {
            logActivity("Could not reindex documents for index " + idx.getId(), e);
        }

    }


    private void logActivity(String activity, Exception e) {
        LOGGER.log(Level.SEVERE, activity, e);
        activityManager.logActivity(activity, EventType.ERROR);
    }

    private void logActivity(String activity) {
        LOGGER.log(Level.FINE, activity);
        activityManager.logActivity(activity, EventType.INFO);
    }

    protected void handleIndexingException(Throwable t) {
        if (t instanceof IndexException && t.getMessage() == null) {
            t = t.getCause();
        }
        activityManager.logActivity("Could not reindex documents: " + t.getMessage(), EventType.ERROR);
        LOGGER.log(Level.SEVERE, "Could not index documents.", t);
    }

    private void handleIndexingException(Index idx, Throwable t) {
        if (t instanceof IndexException && t.getMessage() == null) {
            t = t.getCause();
        }
        activityManager.logActivity("Could not process index " + idx.getId() + ": " + t.getMessage(), EventType.ERROR);
        LOGGER.log(Level.SEVERE, "Could not process index " + idx.getId(), t);
    }
    
    public void index(final ArtifactVersion version) {
        Collection<Index> indices = getIndexes(version);
        
        for (Index idx : indices) {
            ContentHandler ch = contentService.getContentHandler(version.getParent().getContentType());
            
            try {
                getIndexer(idx.getIndexer()).index(version, ch, idx);
            } catch (IndexException e) {
                handleIndexingException(idx, e);
            } catch (IOException e) {
                handleIndexingException(idx, e);
            }
        }
    }

    public Indexer getIndexer(String id) {
        try {
            return (Indexer) context.getBean("indexer." + id);
        } catch (NoSuchBeanDefinitionException e) {
            try {
                return (Indexer) ClassUtils.resolveClassName(id, getClass().getClassLoader()).newInstance();
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }
    }
    
    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    private Registry getRegistry() {
        if (registry == null) {
            registry = (Registry) context.getBean("registry");
        }
        return registry;
    }
    
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }


}
