package org.mule.galaxy.impl.index;

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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.impl.artifact.ArtifactExtension;
import org.mule.galaxy.impl.artifact.ArtifactImpl;
import org.mule.galaxy.impl.jcr.JcrItem;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.JcrWorkspaceManager;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.index.Indexer;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;
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

    private final Log log = LogFactory.getLog(getClass());

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    private ThreadPoolExecutor executor;

    private Registry registry;

    private JcrWorkspaceManager localWorkspaceManager;

    private ApplicationContext context;

    private ActivityManager activityManager;

    private AtomicBoolean destroyed = new AtomicBoolean(false);

    private boolean indexArtifactsAsynchronously = true;
    
    private TypeManager typeManager;
    
    public IndexManagerImpl() throws Exception {
        super(Index.class, "indexes", true);
    }

    @Override
    public void save(Index t) throws DuplicateItemException, NotFoundException {
        save(t, false);
    }

    public void save(Index t, boolean block) throws DuplicateItemException, NotFoundException {
        super.save(t);

        if (block) {
            getIndexer(t).run();
        } else {
            reindex(t);
        }
    }
    
    public Collection<Index> getIndexes() {
        return listAll();
    }

    @Override
    protected String getNodeType() {
        return "galaxy:index";
    }

    @SuppressWarnings("unchecked")
    public Set<Index> getIndexes(final Artifact artifact) {
        return (Set<Index>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                StringBuilder qstr = new StringBuilder("//element(*, galaxy:index)");

                QName dt = artifact.getDocumentType();
                if (dt == null) {
                    qstr.append("[@mediaType=")
                       .append(JcrUtil.stringToXPathLiteralWithQuotes(artifact.getContentType().toString()))
                       .append("]");
                } else {
                    qstr.append("[@documentTypes=")
                        .append(JcrUtil.stringToXPathLiteralWithQuotes(dt.toString()))
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
    public Index getIndexByName(final String name) throws NotFoundException {
        List<Index> indexes = find("description", name);
        if (indexes.size() == 0) {
            throw new NotFoundException(name);
        }
        return indexes.get(0);
    }

    public void delete(final String id, final boolean removeArtifactMetadata) {

        SecurityUtils.doPriveleged(new Runnable() {

            public void run() {
                execute(new JcrCallback() {
                    public Object doInJcr(Session session) throws IOException, RepositoryException {
                        Index idx = doGet(id, session);

                        if (idx.getPropertyDescriptors() != null) {
                            for (PropertyDescriptor pd : idx.getPropertyDescriptors()) {
                                typeManager.deletePropertyDescriptor(pd.getId());
                            }
                        }

                        doDelete(id, removeArtifactMetadata, session);
                        
                        session.save();
                        return null;
                    }
                });
            }
        });
    }

    protected void doDelete(String id, boolean removeArtifactMetadata, Session session) throws RepositoryException {
        Index idx;
        try {
            idx = getIndex(id);
        } catch (NotFoundException e) {
            return;
        }
        
        String propName = idx.getConfiguration().get(XPathIndexer.PROPERTY_NAME);
        
        doDelete(id, session);
        
        if (removeArtifactMetadata && propName != null) {
            Query query = getQueryManager(session).createQuery("//element(*, galaxy:item)[@" + propName + "]", Query.XPATH);
            
            QueryResult result = query.execute();
            
            for (NodeIterator itr = result.getNodes(); itr.hasNext();) {
                Node n = itr.nextNode();
                
                try {
                    new JcrItem(n, getWorkspaceManager()).setProperty(propName, null);
                } catch (PropertyException e) {
                    throw new RuntimeException(e);
                } catch (PolicyException e) {
            handleIndexingException(idx, e);
        } catch (AccessException e) {
            // this should never happen since we're running in priveleged mode
            throw new RuntimeException(e);
                }
            }
            session.save();
        }
    }

    @Override
    public void initialize() throws Exception {
        super.initialize();

        executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, queue);
        executor.prestartAllCoreThreads();
    }

    public void destroy() throws Exception {
        if (log.isDebugEnabled())
        {
            log.debug("Starting IndexManager.destroy() with " + executor.getQueue().size() + " indexing jobs left");
        }
        if (destroyed.get())
        {
            return;
        }

        executor.shutdown();
        destroyed.compareAndSet(false, true);

        executor.awaitTermination(10, TimeUnit.SECONDS);

        // TODO finish reindexing on startup?
        List<Runnable> tasks = executor.shutdownNow();

        if (tasks.size() > 0) {
            log.warn("Could not shut down indexer! Indexing was still going.");
        }
    }

    private void reindex(final Index idx) {
        Runnable runnable = getIndexer(idx);

        addToQueue(runnable);
    }

    private void addToQueue(Runnable runnable) {
        if (!queue.add(runnable))
        {
            handleIndexingException(new Exception("Could not add indexer to queue."));
        }
    }

    /**
     * Returns a Runnable which can be queued or run immediately which indexes a particular artifact.
     * @param item
     * @return
     */
    private Runnable getIndexer(Item item, final String property) {
        final String id = item.getId();
        
        Runnable runnable = new Runnable() {

            public void run() {
                final Session session;
                boolean participate = false;
                SessionFactory sf = getSessionFactory();
                if (TransactionSynchronizationManager.hasResource(sf)) {
                    // Do not modify the Session: just set the participate
                    // flag.
                    participate = true;
                    // get the JCR session from the current TX
                    session = SessionFactoryUtils.getSession(sf, false);
                } else {
                    logger.debug("Opening reeindexing session");
                    session = SessionFactoryUtils.getSession(sf, true);
                    // TODO is this call really required? SFU.getSession() has already bound it to the TX
                    TransactionSynchronizationManager.bindResource(sf, sf.getSessionHolder(session));
                }

                try {
                    SecurityUtils.doPriveleged(new Runnable() {
                        public void run() {
                            try {
                                // lookup a version associated with this session
                            Item item = getRegistry().getItemById(id);

                                try {
                                    doIndex(item, property);
                                } catch (Throwable t) {
                                    handleIndexingException(t);
                                }

                                session.save();
                            } catch (Throwable e) {
                                handleIndexingException(e);
                            } 
                        }
                    });
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
                    // get the JCR session from the current TX
                    session = SessionFactoryUtils.getSession(sf, false);
                } else {
                    logger.debug("Opening reeindexing session");
                    session = SessionFactoryUtils.getSession(sf, true);
                    // TODO is this call really required? SFU.getSession() has already bound it to the TX
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
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
        
        Collection<PropertyDescriptor> filePDs = typeManager.getPropertyDescriptorsForExtension(ArtifactExtension.ID);
        for (PropertyDescriptor pd : filePDs) {
            if (idx.getDocumentTypes() == null || idx.getDocumentTypes().isEmpty())
            {
                // TODO OpRestriction.in causes an NPE on reindexing?!
                q.add(OpRestriction.eq(pd.getProperty() + ".contentType", Arrays.asList(idx.getMediaType())));
            }
            else
            {
                q.add(OpRestriction.in(pd.getProperty() + ".documentType", idx.getDocumentTypes()));
            }
        }
        
        try
        {
            Set<Item> results = getRegistry().search(q).getResults();

            logActivity("Reindexing the \"" + idx.getDescription() + "\" index for " + results.size() + " items.");

            for (Item item : results) {
            // Reindex each file type
                for (PropertyDescriptor pd : filePDs) {
                    PropertyInfo pi = item.getPropertyInfo(pd.getProperty());
                    if (pi != null) {
                        try {
                            getIndexer(idx.getIndexer()).index(item, pi, idx);
                        } catch (IndexException e) {
                            handleIndexingException(idx, e);
                        } catch (IOException e) {
                            handleIndexingException(idx, e);
                        }
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
        log.error(activity, e);
        activityManager.logActivity(activity, EventType.ERROR);
    }

    private void logActivity(String activity) {
        log.info(activity);
        activityManager.logActivity(activity, EventType.INFO);
    }

    protected void handleIndexingException(Throwable t) {
        if (t instanceof IndexException && t.getMessage() == null) {
            t = t.getCause();
        }
        activityManager.logActivity("Could not reindex documents: " + t.getMessage(), EventType.ERROR);
        log.error("Could not index documents.", t);
    }

    private void handleIndexingException(Index idx, Throwable t) {
        if (t instanceof IndexException && t.getMessage() == null) {
            t = t.getCause();
        }
        activityManager.logActivity("Could not process index " + idx.getId() + ": " + t.getMessage(), EventType.ERROR);
        log.error("Could not process index " + idx.getId(), t);
    }
    
    public void index(final Item item) {
    for (PropertyDescriptor pd : typeManager.getPropertyDescriptorsForExtension(ArtifactExtension.ID)) {
            if (indexArtifactsAsynchronously) {
                Runnable indexer = getIndexer(item, pd.getProperty());
                
                addToQueue(indexer);
            } else {
                doIndex(item, pd.getProperty());
            }
    }
    }

    private void doIndex(final Item item, String property) {
        final PropertyInfo pi = item.getPropertyInfo(property);
        if (pi == null) {
            return;
        }

        Artifact a = pi.getValue();
        if (a == null) {
            return;
        }

        final Collection<Index> indices = getIndexes(a);

        SecurityUtils.doPriveleged(new Runnable() {
            public void run() {
                for (Index idx : indices) {
                    try {
                        getIndexer(idx.getIndexer()).index(item, pi, idx);
                    } catch (Throwable e) {
                        handleIndexingException(idx, e);
                    }
                }
            }
        });
        ((ArtifactImpl)a).setIndexed(true);
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
    
    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    private Registry getRegistry() {
        if (registry == null) {
            registry = (Registry) context.getBean("registry");
        }
        return registry;
    }
    
    private JcrWorkspaceManager getWorkspaceManager() {
        if (localWorkspaceManager == null) {
            localWorkspaceManager = (JcrWorkspaceManager) context.getBean("localWorkspaceManager");
        }
        return localWorkspaceManager;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public boolean isIndexArtifactsAsynchronously() {
        return indexArtifactsAsynchronously;
    }

    public void setIndexArtifactsAsynchronously(boolean indexArtifactsAsynchronously) {
        this.indexArtifactsAsynchronously = indexArtifactsAsynchronously;
    }


}
