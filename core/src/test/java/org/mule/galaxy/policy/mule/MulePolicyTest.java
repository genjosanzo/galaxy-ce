package org.mule.galaxy.policy.mule;

import java.io.InputStream;
import java.util.Collection;

import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class MulePolicyTest extends AbstractGalaxyTest {

    public void testRequireHTTPS() throws Exception {
        
        InputStream helloWsdl = getResourceAsStream("/mule/http.xml");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactPolicy p = policyManager.getPolicy(RequireSslPolicy.ID);
        assertNotNull(p);

        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), p);

        try {
            registry.createArtifact(workspace, "application/xml", 
                                    "http.xml", "0.1", helloWsdl,
                                    getAdmin());
            fail("Expected ArtifactPolicyException");
        } catch (ArtifactPolicyException e) {
            Collection<ApprovalMessage> approvals = e.getApprovals();
            for (ApprovalMessage a : approvals) {
                System.out.println(a.getMessage());
            }
            
            assertEquals(1, approvals.size());

            ApprovalMessage a = approvals.iterator().next();
            assertFalse(a.isWarning());
            assertNotNull(a.getMessage());
        }
        
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"/META-INF/applicationContext-core.xml"};
    }
}
