package org.mule.galaxy.impl.upgrade;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.SessionFactory;

public abstract class AbstractSessionUpgrader extends Upgrader {

    private final Log log = LogFactory.getLog(getClass());
    
    private String version;
    private int repositoryVersion;
    private SessionFactory sessionFactory;
    
    public AbstractSessionUpgrader(String version, int repositoryVersion) {
        super();
        this.version = version;
        this.repositoryVersion = repositoryVersion;
    }

    @Override
    public void doUpgrade(final int currentRepositoryVersion, final Session session, final Node root) throws Exception {
        if (currentRepositoryVersion >= repositoryVersion) return;
        
        log.info("Upgrading database to version " + version + "....");
        
        final QueryManager qm = session.getWorkspace().getQueryManager();
        
        JcrUtil.doInTransaction(sessionFactory, new JcrCallback() {

            public Object doInJcr(Session arg0) throws IOException, RepositoryException {
                doUpgrade(root, qm, session);
                return null;
            }
            
        });

        log.info("Database upgrade to version " + version + " complete!");
    }

    protected abstract void doUpgrade(Node root, QueryManager qm, Session session) throws RepositoryException;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }         

}
