package org.mule.galaxy.example.policy;
// START SNIPPET: policy
import java.util.Arrays;
import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.policy.AbstractPolicy;
import org.mule.galaxy.policy.ApprovalMessage;

/**
 * A policy which always fails.
 */
public class AlwaysFailPolicy extends AbstractPolicy {

    public static final String ID = "ALWAYS_FAIL";
    
    private AlwaysFailPolicy() {
        super(ID, "Always fail policy", "A policy which always fails");
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        // Return an approval message which indicates that this policy failed
        // and gives a reason why.
        return Arrays.asList(new ApprovalMessage("The policy failed for this item!", false));
    }
}
// END SNIPPET: policy