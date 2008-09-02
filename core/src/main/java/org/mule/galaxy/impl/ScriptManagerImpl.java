package org.mule.galaxy.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;

public class ScriptManagerImpl extends AbstractReflectionDao<Script> 
    implements ScriptManager, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private AccessControlManager accessControlManager;

    private Map<String, Object> scriptVariables;
    
    public ScriptManagerImpl() throws Exception {
        super(Script.class, "scripts", true);
    }

    @Override
    protected void doInitializeInJcrTransaction(Session session) throws RepositoryException,
        UnsupportedRepositoryOperationException {
        super.doInitializeInJcrTransaction(session);
        
        for (Script s : listAll()) {
            if (s.isRunOnStartup()) {
                try {
                    execute(s.getScript(), s);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public String execute(final String scriptText) throws AccessException, RegistryException {
        return execute(scriptText, null);
    }
    
    public String execute(final String scriptText, Script script) throws AccessException, RegistryException {
        accessControlManager.assertAccess(Permission.EXECUTE_ADMIN_SCRIPTS);
        
        final Binding binding = new Binding();
        binding.setProperty("applicationContext", applicationContext);
        
        for (Map.Entry<String, Object> e : scriptVariables.entrySet()) {
            binding.setProperty(e.getKey(), e.getValue());
        }

        if (script != null) {
            binding.setProperty("script", script);
        }
        
        try {
            return (String)JcrUtil.doInTransaction(getSessionFactory(), new JcrCallback() {

                public Object doInJcr(Session session) throws IOException, RepositoryException {
                    GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
                    Object result = shell.evaluate(scriptText);
                    return result == null ? null : result.toString();
                }
                
            });
        } catch (Exception e1) {
            logger.error(e1);
            throw new RegistryException(e1);
        }
    }

    @Override
    protected void doDelete(String id, Session session) throws RepositoryException {
        // cascade delete all the related jobs
        QueryManager qm = getQueryManager(session);
        
        QueryResult result = qm.createQuery("//element(*, galaxy:scriptJob)[@script='" + id + "']", Query.XPATH).execute();
        
        for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
            nodes.nextNode().remove();
        }
        
        super.doDelete(id, session);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    
    protected String generateNodeName(Script s) {
        return s.getName();
    }
    
    public void setScriptVariables(Map<String, Object> scriptVariables) {
        this.scriptVariables = scriptVariables;
    }
    
}
