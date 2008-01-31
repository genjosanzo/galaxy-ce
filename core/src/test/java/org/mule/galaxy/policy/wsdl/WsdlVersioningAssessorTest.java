package org.mule.galaxy.policy.wsdl;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactResult;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.util.Collection;

public class WsdlVersioningAssessorTest extends AbstractGalaxyTest {
    
    public void testVersioning() throws Exception {
        Artifact a1 = importHelloWsdl();
        ArtifactVersion prev = a1.getActiveVersion();
        
        assertNotNull(a1.getActiveVersion().getData());
        BackwardCompatibilityPolicy assessor = new BackwardCompatibilityPolicy();

        ArtifactResult ar = registry.newVersion(a1,
                                                getResourceAsStream("/wsdl/hello-noOperation.wsdl"), 
                                                "0.2", 
                                                getAdmin());
        ArtifactVersion next = ar.getArtifactVersion();
        
        assertNotNull(next.getData());
        Collection<ApprovalMessage> approvals = assessor.isApproved(a1, prev, next);
        assertEquals(2, approvals.size());
        
        ApprovalMessage app = approvals.iterator().next();
        assertFalse(app.isWarning());
        assertNotNull(app.getMessage());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
}
