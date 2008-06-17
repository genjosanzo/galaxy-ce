package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactCollectionPolicyException;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyInfo;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class PolicyManagerTest extends AbstractGalaxyTest {

    public void testPolicyEnablementFailureOnWorkspace() throws Exception {
        AlwaysFailArtifactPolicy failPolicy = new AlwaysFailArtifactPolicy();
        policyManager.addPolicy(failPolicy);
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        
        Artifact artifact = importHelloWsdl();
        Workspace workspace = artifact.getParent();

        try {
            policyManager.setActivePolicies(workspace, lifecycle, failPolicy);
            fail("Expected policy failure.");
        } catch (ArtifactCollectionPolicyException e) {
            Map<ArtifactVersion, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            assertEquals(1, policyFailures.size());
            // deactivate
            policyManager.setActivePolicies(workspace, lifecycle);
        }

        try {
            policyManager.setActivePolicies(lifecycle, failPolicy);
            fail("Expected policy failure.");
        } catch (ArtifactCollectionPolicyException e) {
            Map<ArtifactVersion, List<ApprovalMessage>> policyFailures = e.getPolicyFailures();
            
            assertEquals(1, policyFailures.size());
            // deactivate
            policyManager.setActivePolicies(lifecycle);
        }
        
        registry.setEnabled(artifact.getDefaultOrLastVersion(), false, getAdmin());

        // this should work now that the artifact is disabled.
        policyManager.setActivePolicies(lifecycle, failPolicy);
    }
    
    public void testPM() throws Exception {
        Collection<ArtifactPolicy> policies = policyManager.getPolicies();
        assertNotNull(policies);
        assertTrue(policies.size() > 1);
        
        ArtifactPolicy policy = policies.iterator().next();
        
        // try lifecycle policies
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();
        policyManager.setActivePolicies(lifecycle, policy);
        
        Artifact artifact = importHelloWsdl();
        Workspace workspace = artifact.getParent();
        
        Collection<ArtifactPolicy> active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(lifecycle);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(lifecycle);
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try workspace activations
        policyManager.setActivePolicies(workspace, lifecycle, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(workspace, lifecycle);
        assertNotNull(active);
        assertEquals(1, active.size());

        active = policyManager.getActivePolicies(workspace, lifecycle.getInitialPhase());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.setActivePolicies(workspace, lifecycle);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        active = policyManager.getActivePolicies(workspace, lifecycle);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try artifact activations
        policyManager.setActivePolicies(artifact, lifecycle, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());
        
        Collection<PolicyInfo> policyInfos = policyManager.getActivePolicies(artifact, false);
        assertEquals(1, policyInfos.size());
        PolicyInfo policyInfo = policyInfos.iterator().next();
        assertNotNull(policyInfo.getAppliesTo());
        assertTrue(policyInfo.getAppliesTo() instanceof Lifecycle);
        
        policyManager.setActivePolicies(artifact, lifecycle);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations
        Phase phase1 = lifecycle.getInitialPhase();
        Phase phase2 = phase1.getNextPhases().iterator().next();
        
        List<Phase> phases1 = Arrays.asList(phase1);
        
        policyManager.setActivePolicies(phases1, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());
        
        active = policyManager.getActivePolicies(phase1);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(phases1);
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try a phase which an artifact isn't in
        List<Phase> phases2 = Arrays.asList(phase2);
        
        policyManager.setActivePolicies(phases2, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        policyManager.setActivePolicies(phases2);
        
        // Try phase activations on workspaces
        policyManager.setActivePolicies(workspace, phases1, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());

        active = policyManager.getActivePolicies(workspace, phase1);
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyManager.setActivePolicies(workspace, phases1);
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
        
        active = policyManager.getActivePolicies(workspace, phase1);
        assertNotNull(active);
        assertEquals(0, active.size());
        
        // Try phase activations on artifacts
        policyManager.setActivePolicies(artifact, phases1, policy);
        
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(1, active.size());
        
        policyInfos = policyManager.getActivePolicies(artifact, false);
        assertEquals(1, policyInfos.size());
        policyInfo = policyInfos.iterator().next();
        assertNotNull(policyInfo.getAppliesTo());
        assertTrue(policyInfo.getAppliesTo() instanceof Phase);
        
        policyManager.setActivePolicies(artifact, phases1);
        active = policyManager.getActivePolicies(artifact.getDefaultOrLastVersion());
        assertNotNull(active);
        assertEquals(0, active.size());
    }

}
