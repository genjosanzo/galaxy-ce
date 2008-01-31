package org.mule.galaxy.mule1.policy;

import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.api.policy.ArtifactPolicy;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class MulePolicyTest extends AbstractGalaxyTest {

    public void testRequireHTTPS() throws Exception {
        
//        InputStream helloWsdl = getResourceAsStream("http-policy-test.xml");
//
//        Collection<Workspace> workspaces = registry.getWorkspaces();
//        assertEquals(1, workspaces.size());
//        Workspace workspace = workspaces.iterator().next();
//
//        ArtifactPolicy p = policyManager.getPolicy(RequireSslPolicy.ID);
//        assertNotNull(p);
//
//        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), p);
//
//        try {
//            registry.createArtifact(workspace, "application/xml",
//                    "http-policy-test.xml", "0.1", helloWsdl,
//                                    getAdmin());
//            fail("Expected ArtifactPolicyException");
//        } catch (ArtifactPolicyException e) {
//            Collection<ApprovalMessage> approvals = e.getApprovals();
//            for (ApprovalMessage a : approvals) {
//                System.out.println(a.getMessage());
//            }
//
//            assertEquals(1, approvals.size());
//
//            ApprovalMessage a = approvals.iterator().next();
//            assertFalse(a.isWarning());
//            assertNotNull(a.getMessage());
//        }
        
    }
}
