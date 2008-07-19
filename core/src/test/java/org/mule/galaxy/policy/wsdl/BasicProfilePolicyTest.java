package org.mule.galaxy.policy.wsdl;

import java.io.InputStream;
import java.util.Collection;

import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class BasicProfilePolicyTest extends AbstractGalaxyTest {

    public void testVersioning() throws Exception {
    
    }
    // disabled due to problems with xerces
    public void xtestVersioning() throws Exception {
        
        InputStream helloWsdl = getResourceAsStream("/wsdl/hello-invalid.wsdl");

        Collection<Workspace> workspaces = registry.getWorkspaces();
        assertEquals(1, workspaces.size());
        Workspace workspace = workspaces.iterator().next();

        ArtifactPolicy p = policyManager.getPolicy(BasicProfilePolicy.WSI_BP_1_1_WSDL);

        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), p);

        try {
            workspace.createArtifact("application/xml", "hello-invalid.wsdl", "0.1", helloWsdl,
                                    getAdmin());
            fail("Expected ArtifactPolicyException");
        } catch (PolicyException e) {
            Collection<ApprovalMessage> approvals = e.getApprovals();
            for (ApprovalMessage a : approvals) {
                System.out.println(a.getMessage());
            }
            
            assertEquals(1, approvals.size());

            ApprovalMessage a = approvals.iterator().next();
            assertFalse(a.isWarning());
            assertNotNull(a.getMessage());
        }
        
        try {
            workspace.createArtifact("application/xml", "hello.wsdl", "0.1", 
                                    getResourceAsStream("/wsdl/wsi/soapbinding/r2710.wsdl"),
                                    getAdmin());
            fail("Expected ArtifactPolicyException");
        } catch (PolicyException e) {
            Collection<ApprovalMessage> approvals = e.getApprovals();
            assertEquals(1, approvals.size());

            ApprovalMessage a = approvals.iterator().next();
            assertFalse(a.isWarning());

            assertNotNull(a.getMessage());
        }
    }

}
