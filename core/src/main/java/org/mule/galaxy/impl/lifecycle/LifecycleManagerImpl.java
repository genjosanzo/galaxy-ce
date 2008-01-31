package org.mule.galaxy.impl.lifecycle;

import org.mule.galaxy.api.ActivityManager;
import org.mule.galaxy.api.ActivityManager.EventType;
import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactPolicyException;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Dao;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.lifecycle.Lifecycle;
import org.mule.galaxy.api.lifecycle.LifecycleManager;
import org.mule.galaxy.api.lifecycle.Phase;
import org.mule.galaxy.api.lifecycle.TransitionException;
import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.api.policy.ArtifactPolicy;
import org.mule.galaxy.api.policy.PolicyManager;
import org.mule.galaxy.api.security.User;
import org.mule.galaxy.api.util.LogUtils;
import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.api.lifecycle.PhaseLogEntry;
import org.mule.galaxy.util.DOMUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LifecycleManagerImpl implements LifecycleManager, ApplicationContextAware {

    private static final String DEFAULT_LIFECYCLE = "Default";
    private static final Logger LOGGER = LogUtils.getL7dLogger(LifecycleManagerImpl.class);

    private List<String> lifecycleDocuments = new ArrayList<String>();
    private Map<String, Lifecycle> lifecycles = new ConcurrentHashMap<String, Lifecycle>();
    private List<ArtifactPolicy> phaseApprovalListeners = new ArrayList<ArtifactPolicy>();
    private Dao<PhaseLogEntry> entryDao;
    private PolicyManager policyManager;
    private JcrTemplate jcrTemplate;
    private ApplicationContext context;
    private ActivityManager activityManager;
    
    public Lifecycle getDefaultLifecycle() {
        return lifecycles.get(DEFAULT_LIFECYCLE);
    }

    public Lifecycle getLifecycle(Workspace workspace) {
        return getDefaultLifecycle();
    }

    public Collection<Lifecycle> getLifecycles() {
        return Collections.unmodifiableCollection(lifecycles.values());
    }

    public void initialize() throws Exception {
        Enumeration<URL> lifecycleUrls = getClass().getClassLoader().getResources("META-INF/galaxy-lifecycles.xml");

        while(lifecycleUrls.hasMoreElements()) {
            URL url = lifecycleUrls.nextElement();
            
            LOGGER.info("Loading lifecycles from " + url.toString());
            
            Map<String, Lifecycle> ls = buildLifecycle(url.openStream());
            lifecycles.putAll(ls);
        }
    }

    private Map<String, Lifecycle> buildLifecycle(InputStream is) throws Exception {
        Document doc = DOMUtils.readXml(is);
        Element root = doc.getDocumentElement();
        
        Map<String, Lifecycle> lifecycles = new HashMap<String, Lifecycle>();
        Element lifecycleEl = (Element) DOMUtils.getChild(root, "lifecycle");
        while (lifecycleEl != null) {
            String name = lifecycleEl.getAttribute("name");
            
            Lifecycle l = new Lifecycle();
            l.setName(name);
            lifecycles.put(name, l);
            
            HashMap<String, Phase> phases = new HashMap<String, Phase>();
            l.setPhases(phases);
            
            Element phaseEl = (Element) DOMUtils.getChild(lifecycleEl, "phase");
            while (phaseEl != null) {
                String phaseName = phaseEl.getAttribute("name");
                Phase phase = new Phase(l);
                phase.setName(phaseName);
                phases.put(phaseName, phase);
                
                phaseEl = (Element) DOMUtils.getNext(phaseEl);
            }
            
            // second pass to link phases
            phaseEl = (Element) DOMUtils.getChild(lifecycleEl, "phase");
            while (phaseEl != null) {
                String phaseName = phaseEl.getAttribute("name");
                String nextPhasesStr = phaseEl.getAttribute("nextPhases");
                Phase p = phases.get(phaseName);
                
                if (nextPhasesStr != null && !"".equals(nextPhasesStr)) {
                    StringTokenizer st = new StringTokenizer(nextPhasesStr, ",");
                    while (st.hasMoreTokens()) {
                        String nextPhaseName = st.nextToken().trim();
                        Phase nextPhase = phases.get(nextPhaseName);
                        
                        if (nextPhase == null) {
                            throw new Exception("PhaseImpl " + nextPhaseName +
                                                " is not a valid transition in phase " +
                                                nextPhase + " in lifecycle " + name);
                        }
                        
                        p.getNextPhases().add(nextPhase);
                    }
                }
                phaseEl = (Element) DOMUtils.getNext(phaseEl);
            }
            
            // Set up initial phases
            String initialPhase = lifecycleEl.getAttribute("initialPhase");
            if (initialPhase == null || "".equals(initialPhase)) {
                throw new Exception("Lifecycle " + name + " must have at least one initial phase!");
            }

            Phase p = phases.get(initialPhase);
            if (p == null) {
                throw new Exception("Initial phase " + name + " isn't a valid phase!");
            }
            l.setInitialPhase(p);
            
            
            lifecycleEl = (Element) DOMUtils.getNext(lifecycleEl, "lifecycle", Node.ELEMENT_NODE);
        }
        return lifecycles;
    }

    public Lifecycle getLifecycle(String lifecycleName) {
        return lifecycles.get(lifecycleName);
    }

    public boolean isTransitionAllowed(Artifact a, Phase p2) {
        Phase p = a.getPhase();
        Lifecycle l = p2.getLifecycle();
        
        if (p == null) {
            return l.getInitialPhase().equals(p2);
        } else {
            return p != null && p.getNextPhases() != null && p.getNextPhases().contains(p2);
        }
    }
    
    public void transition(final Artifact a, 
                           final Phase p,
                           final User user) throws TransitionException, ArtifactPolicyException {
        if (!isTransitionAllowed(a, p)) {
            throw new TransitionException(p);
        }
        
        executeWithPolicyException(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                JcrArtifact ja =(JcrArtifact) a;
                ja.setPhase(p);
                
                ArtifactVersion latest = a.getActiveVersion();
                ArtifactVersion previous = latest.getPrevious();
                
                boolean approved = true;
                List<ApprovalMessage> approvals = policyManager.approve(previous, latest);
                for (ApprovalMessage app : approvals) {
                    if (!app.isWarning()) {
                        approved = false;
                        break;
                    }
                }
                
                if (!approved) {
                    throw new RuntimeException(new ArtifactPolicyException(approvals));
                }
                
                PhaseLogEntry entry = new PhaseLogEntry();
                entry.setUser(user);
                entry.setPhase(p);
                entry.setArtifact(a);
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                entry.setCalendar(cal);
                
                entryDao.save(entry);

                getActivityManager().logActivity(user,
                                                 "Artifact " + ja.getPath() + " was transitioned to phase "
                                                     + p.getName() + " in lifecycle "
                                                     + p.getLifecycle().getName(), EventType.INFO);
                                            
                session.save();
                return null;
            }
            
        });
        
        
    }

    private void executeWithPolicyException(JcrCallback jcrCallback) throws ArtifactPolicyException {
        try {
            jcrTemplate.execute(jcrCallback);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ArtifactPolicyException) {
                throw (ArtifactPolicyException) e.getCause();
            }
            throw e;
        }
    }

    public List<String> getLifecycleDocuments() {
        return lifecycleDocuments;
    }

    public void setLifecycleDocuments(List<String> lifecycleDocuments) {
        this.lifecycleDocuments = lifecycleDocuments;
    }

    public List<ArtifactPolicy> getPhaseApprovalListeners() {
        return phaseApprovalListeners;
    }

    public void setPhaseApprovalListeners(List<ArtifactPolicy> phaseApprovalListeners) {
        this.phaseApprovalListeners = phaseApprovalListeners;
    }

    public ActivityManager getActivityManager() {
        if (activityManager == null) {
            // workaround because spring sucks at circular dependencies
            activityManager = (ActivityManager) context.getBean("activityManager");
        }
        return activityManager;
    }

    public void setPhaseLogEntryDao(Dao<PhaseLogEntry> entryDao) {
        this.entryDao = entryDao;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
}
