package org.mule.galaxy.impl.lifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.ActivityManager.EventType;
import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.lifecycle.PhaseLogEntry;
import org.mule.galaxy.lifecycle.TransitionException;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.security.User;
import org.mule.galaxy.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;


public class LifecycleManagerImpl extends AbstractReflectionDao<Lifecycle> 
    implements LifecycleManager, ApplicationContextAware {

    private static final String NEXT_PHASES = "nextPhases";
    private static final String DEFAULT_LIFECYCLE = "Default";
    private static final Logger LOGGER = LogUtils.getL7dLogger(LifecycleManagerImpl.class);

    private List<ArtifactPolicy> phaseApprovalListeners = new ArrayList<ArtifactPolicy>();
    private Dao<PhaseLogEntry> entryDao;
    private PolicyManager policyManager;
    private ApplicationContext context;
    private ActivityManager activityManager;
    
    public LifecycleManagerImpl() throws Exception {
        super(Lifecycle.class, "lifecycles", false);
    }

    public Lifecycle getDefaultLifecycle() {
        return getLifecycle(DEFAULT_LIFECYCLE);
    }

    public Lifecycle getLifecycle(Workspace workspace) {
        return getDefaultLifecycle();
    }

    public Collection<Lifecycle> getLifecycles() {
        return listAll();
    }

    @Override
    protected void doCreateInitialNodes(Session session, javax.jcr.Node objects) throws RepositoryException {
        if (objects.getNodes().getSize() > 0)
            return;
        
        Node lNode = JcrUtil.getOrCreate(objects, "Default");
        
        Node created = addPhaseNode(lNode, "Created", new String[] { "Developed" });
        created.setProperty("initial", true);
        
        addPhaseNode(lNode, "Developed", new String[] { "Tested" });
        addPhaseNode(lNode, "Tested", new String[] { "Staged", "Deployed", "Retired" });
        addPhaseNode(lNode, "Staged", new String[] { "Deployed", "Retired" });
        addPhaseNode(lNode, "Deployed", new String[] { "Retired" });
        addPhaseNode(lNode, "Retired", new String[0]);
    }

    private Node addPhaseNode(Node lNode, String name, String[] nextPhases) throws ItemExistsException,
        PathNotFoundException, VersionException, ConstraintViolationException, LockException,
        RepositoryException, ValueFormatException {
        Node pNode = lNode.addNode(name);
        pNode.setProperty(NEXT_PHASES, nextPhases);
        return pNode;
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
                List<ApprovalMessage> approvals = getPolicyManager().approve(previous, latest);
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

    protected PolicyManager getPolicyManager() {
        if (policyManager == null) {
            policyManager = (PolicyManager) context.getBean("policyManager");
        }
        return policyManager;
    }

    private void executeWithPolicyException(JcrCallback jcrCallback) throws ArtifactPolicyException {
        try {
            execute(jcrCallback);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ArtifactPolicyException) {
                throw (ArtifactPolicyException) e.getCause();
            }
            throw e;
        }
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

    public Lifecycle getLifecycle(final String lifecycleName) {
        return (Lifecycle) execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = JcrUtil.getOrCreate(getObjectsNode(session), lifecycleName);
                
                if (node == null) {
                    return null;
                }
                
                return build(node, session);
            }
            
        });
    }

    @Override
    public Lifecycle build(Node node, Session session) throws RepositoryException {
        Lifecycle l = new Lifecycle();
        l.setName(node.getName());
        l.setPhases(new HashMap<String,Phase>());
        
        for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
            Node phaseNode = nodes.nextNode();
            
            Phase phase = new Phase(l);
            phase.setName(phaseNode.getName());
            
            l.getPhases().put(phase.getName(), phase);
        }
        
        for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
            Node phaseNode = nodes.nextNode();
            
            Phase phase = l.getPhase(phaseNode.getName());
            
            HashSet<Phase> nextPhases = new HashSet<Phase>();
            try {
                Property property = phaseNode.getProperty(NEXT_PHASES);
                
                for (Value v : property.getValues()) {
                    Phase next = l.getPhase(v.getString());
                    nextPhases.add(next);
                }
                
                phase.setNextPhases(nextPhases);
            } catch (PathNotFoundException e) {
                
            }
            
            try {
                Property property = phaseNode.getProperty("initial");
                
                if (property.getValue().getBoolean()) {
                    l.setInitialPhase(phase);
                }
            } catch (PathNotFoundException e) {
            }
        }
        
        return l;
    }

    @Override
    protected void persist(Lifecycle l, javax.jcr.Node node, Session session) throws Exception {
        Node lNode = JcrUtil.getOrCreate(node, l.getName());
        
        for (Phase p : l.getPhases().values()) {
           Node pNode = JcrUtil.getOrCreate(lNode, p.getName());
           
           ArrayList<String> nextPhases = new ArrayList<String>();
           for (Phase nextPhase : p.getNextPhases()) {
               nextPhases.add(nextPhase.getName());
           }
           
           pNode.setProperty(NEXT_PHASES, nextPhases.toArray(new String[nextPhases.size()]));
        }
    }

    public void setPhaseLogEntryDao(Dao<PhaseLogEntry> entryDao) {
        this.entryDao = entryDao;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
}
