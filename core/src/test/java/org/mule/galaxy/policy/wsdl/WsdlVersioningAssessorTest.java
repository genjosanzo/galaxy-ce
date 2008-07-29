package org.mule.galaxy.policy.wsdl;

import java.util.Collection;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class WsdlVersioningAssessorTest extends AbstractGalaxyTest {
    
    public void testVersioning() throws Exception {
        Artifact a1 = importHelloWsdl();
        ArtifactVersion prev = (ArtifactVersion) a1.getDefaultOrLastVersion();
        
        assertNotNull(((ArtifactVersion) a1.getDefaultOrLastVersion()).getData());
        BackwardCompatibilityPolicy assessor = new BackwardCompatibilityPolicy();

        EntryResult ar = a1.newVersion(getResourceAsStream("/wsdl/hello-noOperation.wsdl"), "0.2", getAdmin());
        ArtifactVersion next = (ArtifactVersion) ar.getEntryVersion();
        
        assertNotNull(next.getData());
        Collection<ApprovalMessage> approvals = assessor.isApproved(next);
        assertEquals(2, approvals.size());
        
        ApprovalMessage app = approvals.iterator().next();
        assertFalse(app.isWarning());
        assertNotNull(app.getMessage());
    }
}
