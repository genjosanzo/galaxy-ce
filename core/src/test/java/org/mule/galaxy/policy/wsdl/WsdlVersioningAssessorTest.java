package org.mule.galaxy.policy.wsdl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.NewItemResult;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.type.TypeManager;

public class WsdlVersioningAssessorTest extends AbstractGalaxyTest {
    
    public void testVersioning() throws Exception {
        Item a1 = importHelloWsdl();
        
        BackwardCompatibilityPolicy assessor = new BackwardCompatibilityPolicy();

        Map<String,Object> props = new HashMap<String, Object>();
        props.put("artifact", new Object[] { getResourceAsStream("/wsdl/hello-noOperation.wsdl"), "application/xml" });
        NewItemResult ar = a1.getParent().newItem("0.2", typeManager.getTypeByName(TypeManager.ARTIFACT_VERSION), props);
        
        Item next = ar.getItem();
        Artifact artifact = next.getProperty("artifact");
        
        assertNotNull(artifact.getData());
        Collection<ApprovalMessage> approvals = assessor.isApproved(next);
        assertEquals(2, approvals.size());
        
        ApprovalMessage app = approvals.iterator().next();
        assertFalse(app.isWarning());
        assertNotNull(app.getMessage());
    }
}