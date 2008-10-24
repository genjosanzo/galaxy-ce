package org.mule.galaxy.example.policy;

import java.util.Arrays;
import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;

/**
 * A policy which always fails.
 */
public class AlwaysFailPolicy implements Policy {

	public static final String ID = "ALWAYS_FAIL";

	public boolean applies(Item item) {
		return true;
	}

	public String getDescription() {
		return "A policy which always fails";
	}

	/**
	 * A unique, unchanging ID for this policy.
	 */
	public String getId() {
		return ID;
	}

	/**
	 * The display name in the UI.
	 */
	public String getName() {
		return "Always fail policy";
	}

	public Collection<ApprovalMessage> isApproved(Item item) {
		// Return an approval message which indicates that this policy failed
		// and gives a reason why.
		return Arrays.asList(new ApprovalMessage("The policy failed for this item!", false));
	}

	public void setRegistry(Registry registry) {
	}

}
