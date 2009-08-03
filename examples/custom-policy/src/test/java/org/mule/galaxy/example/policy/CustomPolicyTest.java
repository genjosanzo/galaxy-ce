package org.mule.galaxy.example.policy;

import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class CustomPolicyTest extends AbstractGalaxyTest {
    public void testPolicyFailure() throws Exception {
        Policy policy = policyManager.getPolicy(AlwaysFailPolicy.ID);

        assertNotNull(policy);

        // Get the default lifecycle
        Lifecycle lifecycle = lifecycleManager.getDefaultLifecycle();

        // Enable the policy
        policyManager.setActivePolicies(lifecycle, policy);

        // Try importing an artifact
        try {
            importHelloWsdl();
            fail("Expected a policy failure!");
        } catch (PolicyException e) {
        }
    }
}
