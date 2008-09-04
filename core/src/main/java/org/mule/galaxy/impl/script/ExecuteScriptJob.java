package org.mule.galaxy.impl.script;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.util.SecurityUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.SessionFactory;

public class ExecuteScriptJob implements Job, ApplicationContextAware {
    public static final String SCRIPT_ID = "scriptId";
    public static final String SESSION_FACTORY = "sessionFactory";
    public static final String SCRIPT_MANAGER = "scriptManager";
    private ApplicationContext context;

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap data = ctx.getJobDetail().getJobDataMap();

        try {
            final SessionFactory sessionFactory = (SessionFactory)context.getBean(SESSION_FACTORY);
            final ScriptManager scriptManager = (ScriptManager)context.getBean(SCRIPT_MANAGER);
            final String scriptId = (String)data.get(SCRIPT_ID);

            SecurityUtils.doPriveleged(new Runnable() {
                public void run() {
                    try {
                        JcrUtil.doInTransaction(sessionFactory, new JcrCallback() {

                            public Object doInJcr(Session session) throws IOException, RepositoryException {
                                try {
                                    Script script = scriptManager.get(scriptId);

                                    scriptManager.execute(script);
                                } catch (AccessException e) {
                                    throw new RuntimeException(e);
                                } catch (RegistryException e) {
                                    throw new RuntimeException(e);
                                } catch (NotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                                return null;
                            }

                        });
                    } catch (Exception e) {
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException)e;
                        }
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof Exception) {
                throw new JobExecutionException(e.getCause());
            }
            throw e;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
