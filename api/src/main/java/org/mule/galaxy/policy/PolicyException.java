package org.mule.galaxy.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Item;

public class PolicyException extends Exception {
    private Map<Item,List<ApprovalMessage>> policyFailures;

    public PolicyException(Map<Item, List<ApprovalMessage>> policyFailures) {
        super();
        this.policyFailures = policyFailures;
    }

    public PolicyException(Item item, String string) {
	this.policyFailures = new HashMap<Item, List<ApprovalMessage>>();
	
	List<ApprovalMessage> msgs = new ArrayList<ApprovalMessage>();
	msgs.add(new ApprovalMessage(string, false));
	
	policyFailures.put(item, msgs);
    }

    public Map<Item, List<ApprovalMessage>> getPolicyFailures() {
        return policyFailures;
    }
}
