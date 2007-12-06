package org.mule.galaxy.wsdl;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class WsdlVersioningAssessorTest extends AbstractGalaxyTest {
    
    public void testVersioning() throws Exception {
        Artifact a1 = importHelloWsdl();
        ArtifactVersion prev = a1.getLatestVersion();
        
        assertNotNull(a1.getLatestVersion().getData());
        BackwardCompatibilityPolicy assessor = new BackwardCompatibilityPolicy();

        ArtifactResult ar = registry.newVersion(a1, 
                                                getResourceAsStream("/wsdl/hello-noOperation.wsdl"), 
                                                "0.2", 
                                                getAdmin());
        ArtifactVersion next = ar.getArtifactVersion();
        
        assertNotNull(next.getData());
        Approval approval = assessor.isApproved(a1, prev, next);
        
        assertFalse(approval.isApproved());
        
        assertEquals(2, approval.getMessages().size());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
}
