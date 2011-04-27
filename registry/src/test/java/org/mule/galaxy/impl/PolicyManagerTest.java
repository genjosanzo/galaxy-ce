package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class PolicyManagerTest extends AbstractGalaxyTest {

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-core-extensions.xml",
            "/META-INF/applicationContext-spring-security.xml",
            "/META-INF/applicationContext-cache.xml",
            "classpath*:/META-INF/galaxy-applicationContext.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }
    
    public void testPolicyEnablementFailureOnWorkspace() throws Exception {
        AlwaysFailPolicy failPolicy = new AlwaysFailPolicy();
        policyManager.addPolicy(failPolicy);
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        
        Item workspace = getTestWorkspace();
        policyManager.setActivePolicies(workspace, lifecycle, failPolicy);
        
        try {
            importHelloWsdl();
            fail("Expected policy failure.");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            // fails once for the artifact and once for the artifact version
            assertEquals(1, policyFailures.size());
            
            // deactivate
            policyManager.setActivePolicies(workspace, lifecycle);
        }

    }

    public void testGlobalPolicyEnablementFailur() throws Exception {
        AlwaysFailPolicy failPolicy = new AlwaysFailPolicy();
        policyManager.addPolicy(failPolicy);
        policyManager.setActivePolicies(failPolicy);
        
        try {
            importHelloWsdl();
            fail("Expected policy failure.");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            // fails once for the artifact and once for the artifact version
            assertEquals(1, policyFailures.size());
        }
    }
    
    public void testPolicyEnablementFailureOnWorkspace2() throws Exception {
        AlwaysFailPolicy failPolicy = new AlwaysFailPolicy();
        policyManager.addPolicy(failPolicy);
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        
        Item workspace = getTestWorkspace();
        Item artifact = importHelloWsdl();

        try {
            policyManager.setActivePolicies(workspace, lifecycle, failPolicy);
            fail("Expected policy failure.");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            // fails once for the artifact and once for the artifact version
            assertEquals(1, policyFailures.size());
            
            assertTrue(policyFailures.keySet().contains(artifact));
            
            // deactivate
            policyManager.setActivePolicies(workspace, lifecycle);
        }

        try {
            policyManager.setActivePolicies(lifecycle, failPolicy);
            fail("Expected policy failure.");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            assertEquals(1, policyFailures.size());
            
            assertTrue(policyFailures.keySet().contains(artifact));
            
            // deactivate
            policyManager.setActivePolicies(lifecycle);
        }
        

        try {
            assertEquals(lifecycle.getInitialPhase(), artifact.getProperty(Registry.PRIMARY_LIFECYCLE));
            
            policyManager.setActivePolicies(Arrays.asList(lifecycle.getInitialPhase()), failPolicy);
            fail("Expected policy failure.");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            assertEquals(1, policyFailures.size());
            
            assertTrue(policyFailures.keySet().contains(artifact));
            
            // deactivate
            policyManager.setActivePolicies(lifecycle);
        }
//        artifact.getDefaultOrLastVersion().setEnabled(false);
//
//        // this should work now that the artifact is disabled.
//        policyManager.setActivePolicies(lifecycle, failPolicy);
    }
    
    public void testPM() throws Exception {
        Collection<Policy> policies = policyManager.getPolicies();
        assertNotNull(policies);
        assertTrue(policies.size() > 1);
        
        Policy policy = policies.iterator().next();
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        policyManager.setActivePolicies(lifecycle, policy);
        
        Item artifact = importHelloWsdl();
        Item workspace = (Item) artifact.getParent();
        
        Collection<?> active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(lifecycle);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(lifecycle);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try workspace activations
        policyManager.setActivePolicies(workspace, lifecycle, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(workspace, lifecycle);
        assertNotNull(active);
        assertEquals(1, active.size());

        active = policyManager.getActivePolicies(workspace, lifecycle.getInitialPhase());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.setActivePolicies(workspace, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        active = policyManager.getActivePolicies(workspace, lifecycle);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try artifact activations
        policyManager.setActivePolicies(artifact, lifecycle, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());

        policyManager.setActivePolicies(artifact, lifecycle);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations
        Phase phase1 = lifecycle.getInitialPhase();
        Phase phase2 = phase1.getNextPhases().iterator().next();
        
        List<Phase> phases1 = Arrays.asList(phase1);
        
        policyManager.setActivePolicies(phases1, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(phase1);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(phases1);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try a phase which an artifact isn't in
        List<Phase> phases2 = Arrays.asList(phase2);
        
        policyManager.setActivePolicies(phases2, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.setActivePolicies(phases2);
        
        // Try phase activations on workspaces
        policyManager.setActivePolicies(workspace, phases1, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());

        active = policyManager.getActivePolicies(workspace, phase1);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(workspace, phases1);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        active = policyManager.getActivePolicies(workspace, phase1);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations on artifacts
        policyManager.setActivePolicies(artifact, phases1, policy);
        
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(1, active.size());

        policyManager.setActivePolicies(artifact, phases1);
        active = policyManager.getActivePolicies(artifact);
        assertNotNull(active);
        assertEquals(0, active.size());
    }

    public void testPoliciesAndEntries() throws Exception {
        AlwaysFailPolicy p = new AlwaysFailPolicy();
        policyManager.addPolicy(p);
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        policyManager.setActivePolicies(lifecycle, p);
        
        Item root = registry.getItems().iterator().next();

        assertEquals(0, root.getItems().size());
        
        try {
            root.newItem("MyService", getSimpleType());
            root.setProperty(Registry.PRIMARY_LIFECYCLE, lifecycle.getInitialPhase());
            registry.save(root);
            fail("There should be a policy exception");
        } catch (PolicyException e) {
            Map<Item, List<ApprovalMessage>> failures = e.getPolicyFailures();
            assertEquals(1, failures.size());
        }
    }
}
