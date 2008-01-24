package org.mule.galaxy.policy.wsdl;

import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.io.InputStream;
import java.util.Collection;

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

        policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), new ArtifactPolicy[] { p });

        try {
            registry.createArtifact(workspace, "application/xml", "hello-invalid.wsdl", "0.1", helloWsdl,
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
        
        try {
            registry.createArtifact(workspace, "application/xml", "hello.wsdl", "0.1", 
                                    getResourceAsStream("/wsdl/wsi/soapbinding/r2710.wsdl"),
                                    getAdmin());
            fail("Expected ArtifactPolicyException");
        } catch (ArtifactPolicyException e) {
            Collection<ApprovalMessage> approvals = e.getApprovals();
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
