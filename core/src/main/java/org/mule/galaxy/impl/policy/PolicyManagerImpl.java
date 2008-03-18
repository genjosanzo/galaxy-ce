package org.mule.galaxy.impl.policy;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemNotFoundException;
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
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactCollectionPolicyException;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyInfo;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.Restriction;
import org.mule.galaxy.query.SearchResults;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class PolicyManagerImpl implements PolicyManager, ApplicationContextAware {
    private Map<String, ArtifactPolicy> policies = new HashMap<String, ArtifactPolicy>();
    private LifecycleManager lifecycleManager;
    private Registry registry;
    private JcrTemplate jcrTemplate;
    private String lifecyclesNodeId;
    private String workspaceLifecyclesNodeId;
    private String artifactsLifecyclesNodeId;
    private String artifactsPhasesNodeId;
    private String workspacePhasesNodeId;
    private String phasesNodeId;
    private ApplicationContext applicationContext;
    
    public void initilaize() throws Exception{
        Session session = jcrTemplate.getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        Node activations = getOrCreate(root, "policyActivations");
        lifecyclesNodeId = getOrCreate(activations, "lifecycles").getUUID();
        phasesNodeId = getOrCreate(root, "phases").getUUID();

        Node artifacts = getOrCreate(activations, "artifacts");
        artifactsLifecyclesNodeId = getOrCreate(artifacts, "lifecycles").getUUID();
        artifactsPhasesNodeId = getOrCreate(artifacts, "phases").getUUID();
        
        Node workspaces = getOrCreate(activations, "workspaces");
        workspaceLifecyclesNodeId = getOrCreate(workspaces, "lifecycles").getUUID();
        workspacePhasesNodeId = getOrCreate(workspaces, "phases").getUUID();
        
        session.save();
    }
    
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        String[] names = ctx.getBeanNamesForType(ArtifactPolicy.class);
        for (String s : names) {
            ArtifactPolicy p = (ArtifactPolicy) ctx.getBean(s);
            addPolicy(p);
        }
        
        this.applicationContext = ctx;
    }
    
    public Registry getRegistry() {
        if (registry == null) {
            registry = (Registry) applicationContext.getBean("registry");
        }
        return registry;
    }

    public void addPolicy(ArtifactPolicy p) {
        policies.put(p.getId(), p);
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    public List<ApprovalMessage> approve(ArtifactVersion previous, 
                                        ArtifactVersion next) {
        Collection<ArtifactPolicy> policies = getActivePolicies(next);
        List<ApprovalMessage> approvals = new ArrayList<ApprovalMessage>();
        for (ArtifactPolicy p : policies) {
            Collection<ApprovalMessage> list = p.isApproved(next.getParent(), previous, next);
            if (list != null) {
                approvals.addAll(list);
            }
        }
        return approvals;
    }


    public ArtifactPolicy getPolicy(String id) {
        return policies.get(id);
    }

    public Collection<ArtifactPolicy> getPolicies() {
        return policies.values();
    }
    
    public void setActivePolicies(Artifact a, Collection<Phase> phases, ArtifactPolicy... policies) 
        throws ArtifactPolicyException {
        for (ArtifactVersion v : a.getVersions()) {
            if (phases.contains(v.getPhase())) {
                approveArtifact(v, policies);
            }
        }
        activatePolicy(artifactsPhasesNodeId, phases, policies, a.getId());
    }

    private void approveArtifact(ArtifactVersion v, ArtifactPolicy... policies) throws ArtifactPolicyException {
        List<ApprovalMessage> messages = approve(v, policies);
        
        if (messages != null) {
            throw new ArtifactPolicyException(messages);
        }
    }

    private List<ApprovalMessage> approve(ArtifactVersion a, ArtifactPolicy... policies) {
        List<ApprovalMessage> messages = null;
        for (ArtifactPolicy p : policies) {
            if (!p.applies(a.getParent())) return null;
            
            Collection<ApprovalMessage> approved = p.isApproved(a.getParent(), a.getPrevious(), a);
            boolean failed = false;
            for (ApprovalMessage m : approved) {
                if (!m.isWarning()) {
                    failed = true;
                    break;
                }
            }
            
            if (failed) {
                if (messages == null) {
                    messages = new ArrayList<ApprovalMessage>();
                }
                messages.addAll(approved);
            }
        }
        return messages;
    }

    public void setActivePolicies(Artifact a, Lifecycle lifecycle, ArtifactPolicy... policies) 
        throws ArtifactPolicyException {
        for (ArtifactVersion v : a.getVersions()) {
            if (lifecycle.getId().equals(v.getPhase().getLifecycle().getId())) {
                approveArtifact(v, policies);
            }
        }
        
        activatePolicy(artifactsLifecyclesNodeId, policies, a.getId(), lifecycle.getId());
    }

    public void setActivePolicies(Collection<Phase> phases, ArtifactPolicy... policies) 
        throws ArtifactCollectionPolicyException, RegistryException {
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query(ArtifactVersion.class);
        q.add(Restriction.in("phase", phases));
        
        approveArtifacts(q, policies);
        
        activatePolicy(phasesNodeId, phases, policies);
    }

    private void approveArtifacts(org.mule.galaxy.query.Query q, ArtifactPolicy... policies)
        throws RegistryException, ArtifactCollectionPolicyException {
        try {
            q.add(Restriction.eq("enabled", true));
            SearchResults results = getRegistry().search(q);
            Map<ArtifactVersion, List<ApprovalMessage>> approvals = null;
            
            for (Object o : results.getResults()) {
                ArtifactVersion a = (ArtifactVersion) o;
                
                List<ApprovalMessage> messages = approve(a, policies);
                if (messages != null) {
                    if (approvals == null) {
                        approvals = new HashMap<ArtifactVersion, List<ApprovalMessage>>();
                    }
                    approvals.put(a, messages);
                }
            }
            
            if (approvals != null) {
                throw new ArtifactCollectionPolicyException(approvals);
            }
        } catch (QueryException e) {
            // this should never happen as we know our query is valid
            throw new RuntimeException(e);
        }
    }

    public void setActivePolicies(Lifecycle lifecycle, ArtifactPolicy... policies) 
        throws RegistryException, ArtifactCollectionPolicyException {
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query(ArtifactVersion.class);
        q.add(Restriction.eq("lifecycle", lifecycle.getName()));
        
        approveArtifacts(q, policies);
        
        activatePolicy(lifecyclesNodeId, policies, lifecycle.getId());
    }

    public void setActivePolicies(Workspace w, Collection<Phase> phases, ArtifactPolicy... policies) {
        activatePolicy(workspacePhasesNodeId, phases, policies, w.getId());
    }

    public void setActivePolicies(Workspace w, Lifecycle lifecycle, ArtifactPolicy... policies) 
        throws RegistryException, ArtifactCollectionPolicyException {
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query(ArtifactVersion.class);
        q.workspaceId(w.getId())
         .add(Restriction.eq("lifecycle", lifecycle.getName()));
        
        approveArtifacts(q, policies);
        
        activatePolicy(workspaceLifecyclesNodeId, policies, w.getId(), lifecycle.getId());
    }
    
    private void activatePolicy(final String nodeId, 
                                final Collection<Phase> phases,
                                final ArtifactPolicy[] policies,
                                final String... nodes) {
        if (phases.size() == 0) {
            return;
        }
        
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(nodeId);
                
                for (String name : nodes) {
                    node = getOrCreate(node, name);
                }
                for (Phase p : phases) {
                    Node lNode = getOrCreate(node, p.getLifecycle().getId());
                    Node pNode = getOrCreate(lNode, p.getName());
                    
                    JcrUtil.removeChildren(pNode);
                    
                    for (ArtifactPolicy policy : policies) {
                        getOrCreate(pNode, policy.getId());
                    }
                }
                
                session.save();
                return null;
            }
        });
    }
    private void activatePolicy(final String nodeId, 
                                final ArtifactPolicy[] policies, 
                                final String... nodes) {
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(nodeId);
                
                for (String name : nodes) {
                    node = getOrCreate(node, name);
                }
                
                JcrUtil.removeChildren(node);
                
                for (ArtifactPolicy policy : policies) {
                    getOrCreate(node, policy.getId());
                }
                
                session.save();
                return null;
            }
        });
    }

    public Collection<PolicyInfo> getActivePolicies(final Artifact a, 
                                                    final boolean includeInherited) {
        final Set<PolicyInfo> activePolicies = new HashSet<PolicyInfo>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = session.getWorkspace().getQueryManager();
                
                addArtifactPhasePolicies(a, activePolicies, qm);
                
                addArtifactLifecyclePolicies(a, activePolicies, qm);
                
                return null;
            }

        });
        return activePolicies;
    }

    public Collection<ArtifactPolicy> getActivePolicies(final Lifecycle l) {
        final Set<ArtifactPolicy> activePolicies = new HashSet<ArtifactPolicy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(lifecyclesNodeId);
                try {

                    node = node.getNode(l.getId());
                    
                    for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
                        activePolicies.add(getPolicy(nodes.nextNode().getName()));
                    }
                } catch (PathNotFoundException e) {
                }
                
                return null;
            }

        });
        return activePolicies;
    }

    public Collection<ArtifactPolicy> getActivePolicies(final Phase p) {
        final Set<ArtifactPolicy> activePolicies = new HashSet<ArtifactPolicy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(phasesNodeId);
                try {

                    node = node.getNode(p.getLifecycle().getId());
                    node = node.getNode(p.getName());
                    
                    for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
                        activePolicies.add(getPolicy(nodes.nextNode().getName()));
                    }
                } catch (PathNotFoundException e) {
                }
                
                return null;
            }

        });
        return activePolicies;
    }

    public Collection<ArtifactPolicy> getActivePolicies(final Workspace w, final Lifecycle l) {
        final Set<ArtifactPolicy> activePolicies = new HashSet<ArtifactPolicy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(workspaceLifecyclesNodeId);
                try {
                    node = node.getNode(w.getId());
                    node = node.getNode(l.getId());
                    
                    for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
                        activePolicies.add(getPolicy(nodes.nextNode().getName()));
                    }
                } catch (PathNotFoundException e) {
                }
                
                return null;
            }

        });
        return activePolicies;
    }

    public Collection<ArtifactPolicy> getActivePolicies(final Workspace w, final Phase p) {
        final Set<ArtifactPolicy> activePolicies = new HashSet<ArtifactPolicy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(workspacePhasesNodeId);
                try {
                    node = node.getNode(w.getId());
                    node = node.getNode(p.getLifecycle().getId());
                    node = node.getNode(p.getName());
                    
                    for (NodeIterator nodes = node.getNodes(); nodes.hasNext();) {
                        activePolicies.add(getPolicy(nodes.nextNode().getName()));
                    }
                } catch (PathNotFoundException e) {
                }
                
                return null;
            }

        });
        return activePolicies;
    }

    public Collection<ArtifactPolicy> getActivePolicies(final ArtifactVersion v) {
        final Set<ArtifactPolicy> activePolicies = new HashSet<ArtifactPolicy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Artifact a = v.getParent();
                String lifecycle = v.getPhase().getLifecycle().getId();
                String workspace = a.getParent().getId();

                addPolicies(activePolicies, a, session, lifecyclesNodeId, lifecycle);
                addPolicies(activePolicies, a, session, workspaceLifecyclesNodeId, 
                            workspace, lifecycle);
                addPolicies(activePolicies, a, session, artifactsLifecyclesNodeId, 
                            a.getId(), lifecycle);
                addPolicies(activePolicies, a, session, phasesNodeId, 
                            lifecycle, v.getPhase().getName());
                addPolicies(activePolicies, a, session, workspacePhasesNodeId, 
                            workspace, lifecycle, v.getPhase().getName());
                addPolicies(activePolicies, a, session, artifactsPhasesNodeId, 
                            a.getId(), lifecycle, v.getPhase().getName());
                
                return null;
            }

        });
        return activePolicies;
    }
    
    private void addPolicies(final Set<ArtifactPolicy> activePolicies,
                             final Artifact artifact,
                             final Session session,
                             final String rootNodeId,
                             String... nodeIds) throws ItemNotFoundException, RepositoryException {
        try {
            Node node = session.getNodeByUUID(rootNodeId);
            for (String name : nodeIds) {
                node = node.getNode(name);
            }
            
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                ArtifactPolicy p = policies.get(itr.nextNode().getName());
                if (p != null && p.applies(artifact)) {
                    activePolicies.add(p);
                }
            }
        } catch (PathNotFoundException e){
            
        }
    }
    
    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    private void addArtifactPhasePolicies(final Artifact a, final Set<PolicyInfo> activePolicies,
                                          QueryManager qm) throws InvalidQueryException, RepositoryException {
        StringBuilder qstr = new StringBuilder();
        qstr.append("//*[@jcr:uuid='")
            .append(artifactsPhasesNodeId)
            .append("']/")
            .append(ISO9075.encode(a.getId()))
            .append("/*");
        Query query = qm.createQuery(qstr.toString(), Query.XPATH);
        
        QueryResult result = query.execute();
        
        for (NodeIterator lifecycles = result.getNodes(); lifecycles.hasNext();) {
            Node lifecycleNode = lifecycles.nextNode();
            
            Lifecycle l = lifecycleManager.getLifecycleById(lifecycleNode.getName());
            
            for (NodeIterator phases = lifecycleNode.getNodes(); phases.hasNext();) {
                Node phasesNode = phases.nextNode();
                Phase phase = l.getPhase(phasesNode.getName());
                
                for (NodeIterator policiesNodes = phasesNode.getNodes(); policiesNodes.hasNext();) { 
                    ArtifactPolicy policy = policies.get(policiesNodes.nextNode().getName());
                    
                    activePolicies.add(new PolicyInfo(policy, phase));
                }
            }
        }
    }

    private void addArtifactLifecyclePolicies(final Artifact a, final Set<PolicyInfo> activePolicies,
                                              QueryManager qm) throws InvalidQueryException, RepositoryException {
        StringBuilder qstr = new StringBuilder();
        qstr.append("//*[@jcr:uuid='")
            .append(artifactsLifecyclesNodeId)
            .append("']/")
            .append(ISO9075.encode(a.getId()))
            .append("/*");
        Query query = qm.createQuery(qstr.toString(), Query.XPATH);
        
        QueryResult result = query.execute();
        
        for (NodeIterator lifecycles = result.getNodes(); lifecycles.hasNext();) {
            Node lifecycleNode = lifecycles.nextNode();
            
            Lifecycle l = lifecycleManager.getLifecycleById(lifecycleNode.getName());
            
            for (NodeIterator policiesNodes = lifecycleNode.getNodes(); policiesNodes.hasNext();) { 
                ArtifactPolicy policy = policies.get(policiesNodes.nextNode().getName());
                
                activePolicies.add(new PolicyInfo(policy, l));
            }
        }
    }
}
