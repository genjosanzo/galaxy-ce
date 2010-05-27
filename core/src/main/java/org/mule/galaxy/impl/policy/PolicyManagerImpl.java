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

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.lifecycle.LifecycleExtension;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.policy.PolicyInfo;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class PolicyManagerImpl implements PolicyManager, ApplicationContextAware {
    private Map<String, Policy> policies = new HashMap<String, Policy>();
    private Registry registry;
    private JcrTemplate jcrTemplate;
    private String lifecyclesNodeId;
    private String itemsLifecyclesNodeId;
    private String itemsPhasesNodeId;
    private String phasesNodeId;
    private ApplicationContext applicationContext;
    private TypeManager typeManager;
    private String activationsNodeId;
    
    public void initilaize() throws Exception{
        Session session = jcrTemplate.getSessionFactory().getSession();
        Node root = session.getRootNode();
        
        Node activations = getOrCreate(root, "policyActivations");
        activationsNodeId = activations.getUUID();
        lifecyclesNodeId = getOrCreate(activations, "lifecycles").getUUID();
        phasesNodeId = getOrCreate(root, "phases").getUUID();

        Node items = getOrCreate(activations, "items");
        itemsLifecyclesNodeId = getOrCreate(items, "lifecycles").getUUID();
        itemsPhasesNodeId = getOrCreate(items, "phases").getUUID();

        session.save();
        session.logout();
    }
    
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        String[] names = ctx.getBeanNamesForType(Policy.class);
        for (String s : names) {
            Policy p = (Policy) ctx.getBean(s);
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

    public void addPolicy(Policy p) {
        policies.put(p.getId(), p);
    }

    public Map<Item, List<ApprovalMessage>> approve(Item item) throws PolicyException, RegistryException {
        return approve(item, false);
    }
    
    protected Map<Item, List<ApprovalMessage>> approve(Item item, boolean delete) throws PolicyException, RegistryException {
        Map<Item, List<ApprovalMessage>> failures = new HashMap<Item, List<ApprovalMessage>>();
        
        approveItem(item, failures, delete, getActivePolicies(item));
        
        boolean approved = true;
        for (List<ApprovalMessage> msgs : failures.values()) {
            for (ApprovalMessage a : msgs) {
                if (!a.isWarning()) {
                    approved = false;
                    break;
                }
            }
        }
        
        if (!approved) {
            throw new PolicyException(failures);
        }
        
        return failures;
    }

    public Map<Item, List<ApprovalMessage>> approveDelete(Item item) throws PolicyException, RegistryException {
        return approve(item, true);
    }

    public Policy getPolicy(String id) {
        return policies.get(id);
    }

    public Collection<Policy> getPolicies() {
        return policies.values();
    }
    
    public void setActivePolicies(Item item, Collection<Phase> phases, Policy... policies) 
        throws PolicyException, RegistryException {
        
        Map<Item, List<ApprovalMessage>> failures = new HashMap<Item, List<ApprovalMessage>>();
        
        approveItem(item, failures, null, phases, policies);
        
        if (failures.size() > 0) {
            throw new PolicyException(failures);
        }
        
        activatePolicy(itemsPhasesNodeId, phases, policies, item.getId());
    }

    private void approveItem(Item item, 
                             Map<Item, List<ApprovalMessage>> failures, 
                             Lifecycle lifecycle,
                             Collection<Phase> phases, 
                             Policy... policies) 
        throws PolicyException, RegistryException {

        approve(item, item, failures, false, lifecycle, phases, policies);
        
        if (item instanceof Item) {
            for (Item i : ((Item) item).getItems()) {
                approveItem(i, failures, lifecycle, phases, policies);
            }
        }
    }

    private void approve(Item itemToApprove, 
                         Item itemWithLifecycle,
                         Map<Item, List<ApprovalMessage>> failures, 
                         boolean delete,
                         Lifecycle lifecycle,
                         Collection<Phase> phases,
                         Policy... policies) {
        for (PropertyInfo pi : itemWithLifecycle.getProperties()) {
            PropertyDescriptor pd = pi.getPropertyDescriptor();
            if (pd != null && pd.getExtension() instanceof LifecycleExtension) {
                Phase p = pi.getValue();

                if (phases != null && phases.contains(p) || lifecycle != null && lifecycle.equals(p.getLifecycle()) || lifecycle == null && phases == null) {
                    List<ApprovalMessage> messages = approve(itemToApprove, delete, policies);
                    
                    if (messages != null && messages.size() > 0) {
                        failures.put(itemToApprove, messages);
                    }
                }
            }
        }
    }

    private void approveItem(Item item, 
                             Map<Item, List<ApprovalMessage>> failures,
                             boolean delete, Collection<PolicyInfo> activePolicies) throws RegistryException {
        approve(item, item, failures, delete, activePolicies);
        
        if (item instanceof Item) {
            for (Item i : ((Item) item).getItems()) {
                approveItem(i, failures, delete, activePolicies);
            }
        }
    }

    private void approve(Item itemToApprove, 
                         Item itemWithLifecycle,
                         Map<Item, List<ApprovalMessage>> failures, 
                         boolean delete, 
                         Collection<PolicyInfo> pis) {
        // policies which apply to lifecycles and phases
        for (PropertyInfo pi : itemWithLifecycle.getProperties()) {
            PropertyDescriptor pd = pi.getPropertyDescriptor();
            if (pd != null && pd.getExtension() instanceof LifecycleExtension) {
                Phase p = pi.getValue();

                for (PolicyInfo policyInfo : pis) {
                    if (policyInfo.appliesTo(p)) {
                        List<ApprovalMessage> messages = approve(itemToApprove, delete, policyInfo.getPolicy());
                        
                        if (messages != null && messages.size() > 0) {
                            failures.put(itemToApprove, messages);
                        }
                    }
                }
            }
        }
        
        // global policies
        for (PolicyInfo pi : pis) {
            if (pi.getAppliesTo() == null) {
                List<ApprovalMessage> messages = approve(itemToApprove, delete, pi.getPolicy());
                
                if (messages != null && messages.size() > 0) {
                    failures.put(itemToApprove, messages);
                }
            }
        }
    }
    
    private List<ApprovalMessage> approve(Item item, boolean delete, Policy... policies) {
        List<ApprovalMessage> messages = null;
        for (Policy p : policies) {
            if (!p.applies(item)) continue;
            
            Collection<ApprovalMessage> approved;
            
            if (delete) {
                approved = p.allowDelete(item);
            } else {
                approved = p.isApproved(item);
            }
            
            boolean failed = false;
            if (approved != null) {
                for (ApprovalMessage m : approved) {
                    if (!m.isWarning()) {
                        failed = true;
                        break;
                    }
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

    public void setActivePolicies(Collection<Phase> phases, Policy... policies) 
        throws PolicyException, RegistryException {
        // TODO don't hard code this
        Collection<PropertyDescriptor> pds = 
            typeManager.getPropertyDescriptorsForExtension("lifecycleExtension");
        if (pds != null) {
            for (PropertyDescriptor pd : pds) {
                org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
                q.add(OpRestriction.in(pd.getProperty() + ".phase", phases));
                
                approveItems(q, null, phases, policies);
            }
        }
        
        activatePolicy(phasesNodeId, phases, policies);
    }

    private void approveItems(org.mule.galaxy.query.Query q, 
                                  Lifecycle lifecycle,
                                  Collection<Phase> phases, 
                                  Policy... policies)
        throws RegistryException, PolicyException {
        try {
            q.add(OpRestriction.not(OpRestriction.eq("enabled", false)));
            SearchResults results = getRegistry().search(q);
            Map<Item, List<ApprovalMessage>> approvals = new HashMap<Item, List<ApprovalMessage>>();
            
            for (Object o : results.getResults()) {
                approveItem((Item) o, approvals, lifecycle, phases, policies);
            }
            
            if (approvals.size() > 0) {
                throw new PolicyException(approvals);
            }
        } catch (QueryException e) {
            // this should never happen as we know our query is valid
            throw new RuntimeException(e);
        }
    }

    public void setActivePolicies(Lifecycle lifecycle, Policy... policies) 
        throws RegistryException, PolicyException {
        
        // TODO don't hard code this
        Collection<PropertyDescriptor> pds = 
            typeManager.getPropertyDescriptorsForExtension("lifecycleExtension");
        if (pds != null) {
            for (PropertyDescriptor pd : pds) {
                org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
                q.add(OpRestriction.eq(pd.getProperty() + ".id", lifecycle.getId()));
                
                approveItems(q, lifecycle, null, policies);
            }
        }
        
        activatePolicy(lifecyclesNodeId, policies, lifecycle.getId());
    }
    

    public void setActivePolicies(Policy... policies) 
        throws RegistryException, PolicyException {
        
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
        
        approveItems(q, null, null, policies);
        
        activatePolicy(activationsNodeId, policies);
    }    

    public void setActivePolicies(Item item, Lifecycle lifecycle, Policy... policies) 
        throws RegistryException, PolicyException {
        Map<Item, List<ApprovalMessage>> failures = new HashMap<Item, List<ApprovalMessage>>();
        
        approveItem(item, failures, lifecycle, null, policies);
        
        if (failures.size() > 0) {
            throw new PolicyException(failures);
        }
        
        activatePolicy(itemsLifecyclesNodeId, policies, item.getId(), lifecycle.getId());
    }
    
    private void activatePolicy(final String nodeId, 
                                final Collection<Phase> phases,
                                final Policy[] policies,
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
                    Node pNode = getOrCreate(node, p.getId());
                    
                    JcrUtil.removeChildren(pNode);
                    
                    for (Policy policy : policies) {
                        getOrCreate(pNode, policy.getId());
                    }
                }
                
                session.save();
                return null;
            }
        });
    }
    private void activatePolicy(final String nodeId, 
                                final Policy[] policies, 
                                final String... nodes) {
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(nodeId);
                
                for (String name : nodes) {
                    node = getOrCreate(node, name);
                }
                
                JcrUtil.removeChildren(node);
                
                for (Policy policy : policies) {
                    getOrCreate(node, policy.getId());
                }
  
                session.save();
                return null;
            }
        });
    }
    
    public Collection<Policy> getActivePolicies(final Lifecycle l) {
        final Set<Policy> activePolicies = new HashSet<Policy>();
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

    public Collection<Policy> getActivePolicies(final Phase p) {
        final Set<Policy> activePolicies = new HashSet<Policy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = session.getNodeByUUID(phasesNodeId);
                try {

                    node = node.getNode(p.getId());
                    
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

    public Collection<Policy> getActivePolicies(final Item item, final Lifecycle lifecycle) {
        final Set<Policy> activePolicies = new HashSet<Policy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                // Add policies which apply to the lifecycle
                activePolicies.addAll(getPolicies(session, lifecyclesNodeId, lifecycle.getId()));

                activePolicies.addAll(getPolicies(session, itemsLifecyclesNodeId, item.getId(), lifecycle
                    .getId()));

                return null;
            }

        });
        return activePolicies;
    }

    public Collection<Policy> getActivePolicies(final Item item, final Phase phase) {
        final Set<Policy> activePolicies = new HashSet<Policy>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                // Add policies which apply to the phase
                activePolicies.addAll(getPolicies(session, phasesNodeId, phase.getId()));

                // Add policies which apply to this item and phase
                activePolicies.addAll(getPolicies(session, itemsPhasesNodeId, item.getId(), phase.getId()));

                return null;
            }

        });
        return activePolicies;
    }

    public Collection<PolicyInfo> getActivePolicies(final Item item) {
        final Set<PolicyInfo> activePolicies = new HashSet<PolicyInfo>();
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                // add global policies
                add(activePolicies, getPolicies(session, activationsNodeId), null);
                
                for (PropertyInfo pi : item.getProperties()) {
                    PropertyDescriptor pd = pi.getPropertyDescriptor();
                    if (pd != null && pd.getExtension() instanceof LifecycleExtension) {
                        Phase phase = pi.getValue();
                        Lifecycle lifecycle = phase.getLifecycle();
                        
                        // Add policies which apply to the lifecycle
                        add(activePolicies, 
                            getPolicies(session, lifecyclesNodeId, lifecycle.getId()),
                            lifecycle);
                        
                        // Add policies which apply to the phase
                        add(activePolicies,
                            getPolicies(session, phasesNodeId, phase.getId()),
                            phase);
                        
                        // Loop through the item and it's parents and apply the policies.
                        Item policyItem = item;
                        while (policyItem != null) {
                            add(activePolicies,
                                getPolicies(session, itemsLifecyclesNodeId, policyItem.getId(), lifecycle.getId()),
                                lifecycle);
                            
                            // Add policies which apply to this item and lifecycle
                            add(activePolicies,
                                getPolicies(session, itemsPhasesNodeId, policyItem.getId(), phase.getId()),
                                phase);
                                
                            policyItem = policyItem.getParent();
                        }
                    }
                }

                return null;
            }

        });
        return activePolicies;
    }

    protected void add(Set<PolicyInfo> activePolicies, Collection<Policy> policies2, Object appliesTo) {
        for (Policy p : policies2) {
            activePolicies.add(new PolicyInfo(p, appliesTo));
        }
    }

    protected void addPoliciesForItem(final Set<Policy> activePolicies, final Item item,
                                    Session session, Phase phase, Lifecycle lifecycle)
        throws ItemNotFoundException, RepositoryException {
        // Add policies which apply to this item and lifecycle
        activePolicies.addAll(getPolicies(session, itemsLifecyclesNodeId, item.getId(), lifecycle.getId()));
        
        // Add policies which apply to this item and lifecycle
        activePolicies.addAll(getPolicies(session, itemsPhasesNodeId, item.getId(), phase.getId()));
    }
    
    protected Collection<Policy> getPolicies(final Session session,
                                             final String rootNodeId,
                                             String... nodeIds) throws ItemNotFoundException, RepositoryException {
        HashSet<Policy> activePolicies = new HashSet<Policy>();
        try {
            Node node = session.getNodeByUUID(rootNodeId);
            for (String name : nodeIds) {
                node = node.getNode(name);
            }
            
            for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                Policy p = policies.get(itr.nextNode().getName());
                if (p != null) {
                    activePolicies.add(p);
                }
            }
        } catch (PathNotFoundException e){
            
        } catch (ItemNotFoundException e){
            
        }
        return activePolicies;
    }
    
    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }
    
}
